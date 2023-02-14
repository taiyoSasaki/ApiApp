package jp.techacademy.taiyo.sasaki.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {
    private var isFavorite = true
    private lateinit var shop :Shop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        shop = getCustomExtra()

        webView.loadUrl(if (shop.couponUrls.sp.isNotEmpty()) shop.couponUrls.sp else shop.couponUrls.pc)

        // お気に入り状態を取得
        isFavorite = FavoriteShop.findBy(shop.id) != null
        //名前をセット
        nameTextView.text = shop.name
        // Picassoライブラリを使い、imageViewにdata.logoImageのurlの画像を読み込ませる
        Picasso.get().load(shop.logoImage).into(imageView)
        // 白抜きの星マークの画像を指定
        favoriteImageView.apply {
            setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border) // Picassoというライブラリを使ってImageVIewに画像をはめ込む
            setOnClickListener {
                if (isFavorite) {
                    onDeleteFavorite(shop.id)
                } else {
                    onAddFavorite(shop)
                }
            }
        }

    }

    private fun onAddFavorite(shop: Shop) { // Favoriteに追加するときのメソッド(Fragment -> Activity へ通知する)
        FavoriteShop.insert(FavoriteShop().apply {
            id = shop.id
            name = shop.name
            imageUrl = shop.logoImage
            url = if (shop.couponUrls.sp.isNotEmpty()) shop.couponUrls.sp else shop.couponUrls.pc
        })
        updateView()
    }

    private fun onDeleteFavorite(id: String) { // Favoriteから削除するときのメソッド(Fragment -> Activity へ通知する)
        showConfirmDeleteFavoriteDialog(id)
    }

    private fun showConfirmDeleteFavoriteDialog(id: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_favorite_dialog_title)
            .setMessage(R.string.delete_favorite_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                deleteFavorite(id)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->}
            .create()
            .show()
    }

    private fun deleteFavorite(id :String) {
        FavoriteShop.delete(id)
        updateView()
    }

    private fun updateView() {
        isFavorite = FavoriteShop.findBy(shop.id) != null
        favoriteImageView.setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border) // Picassoというライブラリを使ってImageVIewに画像をはめ込む
    }

    companion object {
        private const val KEY_URL_SP = "key_url_sp"
        private const val KEY_URL_PC = "key_url_pc"
        private const val KEY_ID = "key_id"
        private const val KEY_LOGO_IMAGE = "key_logoImage"
        private const val KEY_NAME = "key_name"

        lateinit var intent: Intent

        fun start(activity: Activity, item: Shop) {
            intent = Intent(activity, WebViewActivity::class.java)
            putCustomExtra(item)
            activity.startActivity(intent)
        }

        fun putCustomExtra(item: Shop) {
            intent.putExtra(KEY_URL_SP, item.couponUrls.sp)
            intent.putExtra(KEY_URL_PC, item.couponUrls.pc)
            intent.putExtra(KEY_ID, item.id)
            intent.putExtra(KEY_LOGO_IMAGE, item.logoImage)
            intent.putExtra(KEY_NAME, item.name)
        }

        fun getCustomExtra() :Shop {
            val shop = Shop(
                couponUrls = CouponUrls(sp = intent.getStringExtra(KEY_URL_SP).toString(),pc = intent.getStringExtra(KEY_URL_PC).toString()),
                id = intent.getStringExtra(KEY_ID).toString(),
                logoImage = intent.getStringExtra(KEY_LOGO_IMAGE).toString(),
                name = intent.getStringExtra(KEY_NAME).toString()
            )
            return shop
        }
    }
}


