package engineer.kaobei.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.material.transition.MaterialSharedAxis
import engineer.kaobei.BuildConfig
import engineer.kaobei.R
import engineer.kaobei.util.CustomTabUtil
import engineer.kaobei.viewmodel.IndexViewModel

class IndexFragment : Fragment() {

    companion object {
        val URL_KOBEENGINEER_ANIMAL_KOHLRABI = "https://kaobei.engineer/animal/kohlrabi"
        val URL_KOBEENGINEER_FACEBOOK = "https://www.facebook.com/init.kobeengineer/"
        val URL_KOBEENGINEER_PLURK = "https://plurk.com/kaobei_engineer/"
        val URL_KOBEENGINEER_TWITTER = "https://twitter.com/kaobei_engineer/"
        val URL_KOBEENGINEER_REVIEW = "https://kaobei.engineer/cards/review"
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
            CustomTabUtil.createCustomTab(view.context, URL_KOBEENGINEER_ANIMAL_KOHLRABI)
        }

        card2.setOnClickListener {
            CustomTabUtil.createCustomTab(view.context, URL_KOBEENGINEER_FACEBOOK)
        }

        card3.setOnClickListener {
            CustomTabUtil.createCustomTab(view.context, URL_KOBEENGINEER_PLURK)
        }

        card4.setOnClickListener {
            CustomTabUtil.createCustomTab(view.context, URL_KOBEENGINEER_TWITTER)
        }
        loadADS(view.context, view)
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

    fun loadADS(context: Context, view:View){
        val ads_style1 : UnifiedNativeAdView = view.findViewById(R.id.ads_style1)
        ads_style1.visibility = View.GONE
        val ads_style1_img : ImageView = view.findViewById(R.id.ads_style1_img)
        val ads_style1_text1 : TextView = view.findViewById(R.id.ads_style1_text1)
        val ads_style1_text2 : TextView = view.findViewById(R.id.ads_style1_text2)
        val adLoader = AdLoader.Builder(context,BuildConfig.ADMOB_1)
            .forUnifiedNativeAd { ad : UnifiedNativeAd ->
                ads_style1.visibility = View.VISIBLE
                ads_style1_text1.text = ad.headline
                ads_style1_text2.text = ad.body
                if (ad.images.isNotEmpty()) {
                    //Glide.with(context).load(ad.images[0].uri).into(ads_style1_img)
                }
                ads_style1.headlineView = ads_style1_text1
                ads_style1.bodyView = ads_style1_text2
                //ads_style1.imageView = ads_style1_img
                ads_style1.setNativeAd(ad)
                // If this callback occurs after the activity is destroyed, you
                // must call destroy and return or you may get a memory leak.
                // Note `isDestroyed` is a method on Activity.
                if (activity?.isDestroyed!!) {
                    ads_style1.destroy()
                    return@forUnifiedNativeAd
                }
            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    ads_style1.visibility = View.GONE
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build())
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

}
