package engineer.kaobei.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.facebook.ads.*
import com.google.android.material.transition.MaterialSharedAxis
import engineer.kaobei.BuildConfig
import engineer.kaobei.R
import engineer.kaobei.Util.CustomTabUtil
import engineer.kaobei.Viewmodel.IndexViewModel

class IndexFragment : Fragment() {

    companion object {
        fun newInstance() = IndexFragment()
    }

    private lateinit var viewModel: IndexViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_index, container,
            false
        )
        val card1: CardView = view.findViewById(R.id.card1)
        val card2: CardView = view.findViewById(R.id.card2)
        val card3: CardView = view.findViewById(R.id.card3)
        val card4: CardView = view.findViewById(R.id.card4)
        card1.setOnClickListener {
            CustomTabUtil.createCustomTab(view.context, "https://kaobei.engineer/animal/kohlrabi")
        }
        card2.setOnClickListener {
            CustomTabUtil.createCustomTab(
                view.context,
                "https://www.facebook.com/init.kobeengineer/"
            )
        }
        card3.setOnClickListener {
            CustomTabUtil.createCustomTab(view.context, "https://plurk.com/kaobei_engineer/")
        }
        card4.setOnClickListener {
            CustomTabUtil.createCustomTab(view.context, "https://twitter.com/kaobei_engineer/")
        }
        loadNativeAd(view.context, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(IndexViewModel::class.java)
        val forward: MaterialSharedAxis = MaterialSharedAxis.create(MaterialSharedAxis.X, true)
        val backward: MaterialSharedAxis = MaterialSharedAxis.create(MaterialSharedAxis.X, false)
        enterTransition = forward
        exitTransition = backward
    }

    private fun loadNativeAd(context: Context, view: View) {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        val nativeAd = NativeAd(context, BuildConfig.AUDIENCE_1)
        val TAG = "TAG"
        val loadAdConfig = nativeAd.buildLoadAdConfig()
            .withAdListener(object : NativeAdListener {
                override fun onAdClicked(p0: Ad?) {
                    Log.d(TAG, "Native ad clicked!")
                }

                override fun onMediaDownloaded(p0: Ad?) {
                    Log.e(TAG, "Native ad finished downloading all assets.")
                }

                override fun onError(p0: Ad?, p1: AdError?) {
                    Log.e(TAG, "Native ad failed to load: " + p1?.errorMessage)
                }

                override fun onAdLoaded(p0: Ad?) {
                    Log.d(TAG, "Native ad is loaded and ready to be displayed!")
                    if (p0 == null || nativeAd != p0) {
                        return;
                    }
                    inflateAd(view, nativeAd)
                }

                override fun onLoggingImpression(p0: Ad?) {
                    Log.d(TAG, "Native ad impression logged!")
                }

            })
            .build()
        // Request an ad
        nativeAd.loadAd(loadAdConfig)
    }

    private fun inflateAd(view: View, nativeAd: NativeAd) {
        nativeAd.unregisterView()
        val nativeAdLayout: NativeAdLayout = view.findViewById(R.id.native_ad_container);
        val inflater = LayoutInflater.from(view.context)
        val adView = inflater.inflate(R.layout.ads_style2, nativeAdLayout, false) as LinearLayout?
        nativeAdLayout.addView(adView)

        // Add the AdOptionsView
        val adChoicesContainer: LinearLayout? = adView?.findViewById(R.id.ad_choices_container)
        val adOptionsView =
            AdOptionsView(view.context, nativeAd, nativeAdLayout)
        adChoicesContainer?.removeAllViews()
        adChoicesContainer?.addView(adOptionsView, 0)

        // Create native UI using the ad metadata.

        // Create native UI using the ad metadata.
        val nativeAdIcon: AdIconView = adView!!.findViewById(R.id.native_ad_icon)
        val nativeAdTitle = adView!!.findViewById<TextView>(R.id.native_ad_title)
        val nativeAdMedia: MediaView = adView!!.findViewById(R.id.native_ad_media)
        val nativeAdSocialContext =
            adView!!.findViewById<TextView>(R.id.native_ad_social_context)
        val nativeAdBody = adView!!.findViewById<TextView>(R.id.native_ad_body)
        val sponsoredLabel = adView!!.findViewById<TextView>(R.id.native_ad_sponsored_label)
        val nativeAdCallToAction: Button = adView!!.findViewById(R.id.native_ad_call_to_action)

        // Set the Text.

        // Set the Text.
        nativeAdTitle.text = nativeAd.advertiserName
        nativeAdBody.text = nativeAd.adBodyText
        nativeAdSocialContext.text = nativeAd.adSocialContext
        nativeAdCallToAction.setVisibility(if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE)
        nativeAdCallToAction.setText(nativeAd.adCallToAction)
        sponsoredLabel.text = nativeAd.sponsoredTranslation

        // Create a list of clickable views

        // Create a list of clickable views
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)

        // Register the Title and CTA button to listen for clicks.

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
            adView,
            nativeAdMedia,
            nativeAdIcon,
            clickableViews
        )
    }

}
