package com.dovoo.memesnetwork.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.dovoo.memesnetwork.MainActivity
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.MemesRecyclerViewAdapter
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.billing.BillingManager
import com.dovoo.memesnetwork.billing.BillingManager.BillingUpdatesListener
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.components.MyLinearLayoutManager
import com.dovoo.memesnetwork.utils.AdUtils.loadAds
import com.dovoo.memesnetwork.utils.AdUtils.loadInterstitialAds
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils.getPrefs
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.floatingactionbutton.FloatingActionButton
import im.ene.toro.widget.Container
import im.ene.toro.widget.PressablePlayerSelector
import java.util.*

class NewestMemesFragment : Fragment(), BillingUpdatesListener {
    private var loadingBar: FrameLayout? = null
    private var section: String? = null
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var container: Container
    var layoutManager: MyLinearLayoutManager? = null
    var adapter: MemesRecyclerViewAdapter? = null
    var selector: PressablePlayerSelector? = null
    private val directLinkItemTestList: MutableList<DirectLinkItemTest> = ArrayList()

    //private IInAppBillingService mService;
    private var billingManager: BillingManager? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var mServiceConn: ServiceConnection? = null
    private var mAdView: AdView? = null
    lateinit var fab: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            section = bundle.getString("section", null)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new, viewGroup, false)
        billingManager = BillingManager(this)
        setupBillingService()
        mAdView = view.findViewById(R.id.adView)
        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd!!.adUnitId = "ca-app-pub-4908922088432819/1640263467"
        mInterstitialAd!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd!!.show()
            }

            override fun onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                Toast.makeText(
                    context,
                    "To remove ads, click button remove ads on your profile",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        container = view.findViewById(R.id.player_container)
        loadingBar = view.findViewById(R.id.loadingBar)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        fab = view.findViewById(R.id.fab)
        layoutManager = MyLinearLayoutManager(context)
        container.setLayoutManager(layoutManager)
        selector = PressablePlayerSelector(container)
        container.setPlayerSelector(selector)
        adapter = MemesRecyclerViewAdapter(
            requireContext(),
            selector,
            directLinkItemTestList,
            (activity as MainActivity?)!!.loadingBar
        )
        container.setAdapter(adapter)
        layoutManager?.let {
            container.addOnScrollListener(object : EndlessRecyclerViewScrollListener(it) {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    fetchData(totalItemsCount)
                }
            })
        }

        swipeRefreshLayout.setOnRefreshListener(
            OnRefreshListener {
                swipeRefreshLayout.setVisibility(View.GONE)
                fetchData(0)
            }
        )
        fab.setOnClickListener(View.OnClickListener {
            val smoothScrollerToTop: SmoothScroller = object : LinearSmoothScroller(context) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }
            }
            smoothScrollerToTop.targetPosition = 0
            layoutManager!!.startSmoothScroll(smoothScrollerToTop)
        })
        fetchData(0)
        return view
    }

    private fun setupBillingService() {
        mServiceConn = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName) {
                // mService = null;
            }

            override fun onServiceConnected(
                name: ComponentName,
                service: IBinder
            ) {
                // mService = IInAppBillingService.Stub.asInterface(service);
                checkMemberIsPremium()
            }
        }
        val i = Intent("com.android.vending.billing.InAppBillingService.BIND")
        i.setPackage("com.android.vending")
        requireContext().bindService(
            i,
            mServiceConn!!, Context.BIND_AUTO_CREATE
        )
    }

    fun checkMemberIsPremium() {
        val isPremiumMember = false
        val packageName = "com.dovoo.memesnetwork"
        //        try {
//            Bundle ownedItems = mService.getPurchases(3, packageName, "subs", null);
//            int response = ownedItems.getInt("RESPONSE_CODE");
//            if (response == 0) {
//                ArrayList ownedSkus =
//                        ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
//                ArrayList purchaseDataList =
//                        ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
//
//                for (int i = 0; i < purchaseDataList.size(); ++i) {
//                    String sku = (String) ownedSkus.get(i);
//                    if (sku.equals("premium_member")) {
//                        isPremiumMember = true;
//                    }
//                }
//
//                // if continuationToken != null, call getPurchases again
//                // and pass in the token to retrieve more items
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        billingManager!!.updateMemberStatus(context, isPremiumMember)
    }

    private fun fetchData(offset: Int) {
        loadingBar!!.visibility = View.VISIBLE
        val min = 1
        val max = 100
        val randomNum = Random().nextInt(max - min + 1) + min
        if (randomNum > 30) {
            loadInterstitialAds(requireContext(), mInterstitialAd!!)
        }
        if (offset == 0) {
            directLinkItemTestList.clear()
        }
        val param: MutableMap<String, String> = HashMap()
        param["offset"] = offset.toString() + ""
        param["user_id"] = getPrefs(requireContext())
            .getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0).toString() + ""
        if (section != null && !section.equals("all", ignoreCase = true)) {
            param["post_section"] = section!!
        }

//        AndroidNetworking.get(BuildConfig.API_URL + "index")
//                .addQueryParameter(param)
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsJSONArray(new JSONArrayRequestListener() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        // do anything with response
//                        try {
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject result = response.getJSONObject(i);
//                                Integer id = result.getInt("id");
//                                String title = result.getString("title");
//                                String type = result.getString("type");
//                                JSONObject imagesObject = new JSONObject(result.getString("images"));
//                                String coverUrl = imagesObject.getJSONObject("image700").getString("url");
//                                String category = result.getString("post_section");
//                                String videoUrl = null;
//                                boolean isVideo = false;
//                                boolean hasAudio = false;
//                                if (type.equalsIgnoreCase("animated")) {
//                                    isVideo = true;
//                                    videoUrl = imagesObject.getJSONObject("image460sv").getString("url");
//                                    hasAudio = (imagesObject.getJSONObject("image460sv").getInt("hasAudio") == 1 ? true : false);
//                                }
//
//                                int width = imagesObject.getJSONObject("image700").getInt("width");
//                                int height = imagesObject.getJSONObject("image700").getInt("height");
//                                Map<String, Object> data = new HashMap<>();
//                                data.put("total_like", result.get("total_like"));
//                                data.put("total_dislike", result.get("total_dislike"));
//                                data.put("total_comment", result.get("total_comment"));
//                                data.put("is_liked", result.get("is_liked"));
//
//                                directLinkItemTestList.add(new DirectLinkItemTest(id, category, title, videoUrl, data, Picasso.get(), coverUrl, width, height, hasAudio, isVideo));
//                            }
//                            adapter.notifyDataSetChanged();
//                            swipeRefreshLayout.setEnabled(true);
//                            swipeRefreshLayout.setRefreshing(false);
//                            swipeRefreshLayout.setVisibility(View.VISIBLE);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        loadingBar.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onError(ANError error) {
//                        // handle error
//                        System.out.print("a");
//                        loadingBar.setVisibility(View.GONE);
//                        swipeRefreshLayout.setRefreshing(false);
//                        swipeRefreshLayout.setVisibility(View.VISIBLE);
//
//                    }
//                });
    }

    override fun onResume() {
        super.onResume()
        loadAds(requireContext(), mAdView!!)
    }

    override fun onDestroyView() {
        layoutManager = null
        adapter = null
        selector = null
        super.onDestroyView()
    }

    override fun onSubscriptionPurchaseUpdated() {
        loadAds(requireContext(), mAdView!!)
        loadInterstitialAds(requireContext(), mInterstitialAd!!)
    }
}