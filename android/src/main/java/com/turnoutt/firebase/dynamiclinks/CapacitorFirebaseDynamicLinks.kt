package com.turnoutt.firebase.dynamiclinks

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

@CapacitorPlugin
class CapacitorFirebaseDynamicLinks : Plugin() {
    @PluginMethod
    fun createDynamicLink(call: PluginCall) {

        try {
            val builder = FirebaseDynamicLinks.getInstance()
                    .createDynamicLink()
                    .setDomainUriPrefix(call.getString("domainUriPrefix")!!)
                    .setLink(Uri.parse(call.getString("uri")))

            buildAndroidParameters(call, builder)
            buildIOSParameters(call, builder)
            buildGoogleAnalyticsParameters(call, builder)
            buildITunesParameters(call, builder)
            buildSocialMetaParameters(call, builder)

            val dynamicLink = builder.buildDynamicLink()

            val ret = JSObject()
            ret.put("value", dynamicLink.uri)
            call.success(ret)
        } catch (ex: Exception) {
            call.error("Unable to create DynamicLink", ex)
        }
    }

    @PluginMethod
    fun createDynamicShortLink(call: PluginCall) {

        val builder = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setDomainUriPrefix(call.getString("domainUriPrefix")!!)

                .setLink(Uri.parse(call.getString("uri")))

        buildAndroidParameters(call, builder)
        buildIOSParameters(call, builder)
        buildGoogleAnalyticsParameters(call, builder)
        buildITunesParameters(call, builder)
        buildSocialMetaParameters(call, builder)

        val ret = JSObject()

        builder.buildShortDynamicLink().addOnSuccessListener {
            ret.put("value", it.shortLink)
            call.success(ret)
        }.addOnFailureListener {
            call.error(it.localizedMessage)
        }
    }

    private fun buildGoogleAnalyticsParameters(call: PluginCall, builder: DynamicLink.Builder) {
        val googleAnalyticsParameters = call.getObject("googleAnalytics")
        if (googleAnalyticsParameters != null) {
            val googleAnalyticsParametersBuilder = DynamicLink.GoogleAnalyticsParameters.Builder()

            if (googleAnalyticsParameters.getString("source") != null) {
                googleAnalyticsParametersBuilder.source = googleAnalyticsParameters.getString("source")!!
            }

            if (googleAnalyticsParameters.getString("medium") != null) {
                googleAnalyticsParametersBuilder.medium = googleAnalyticsParameters.getString("medium")!!
            }

            if (googleAnalyticsParameters.getString("campaign") != null) {
                googleAnalyticsParametersBuilder.campaign = googleAnalyticsParameters.getString("campaign")!!
            }

            if (googleAnalyticsParameters.getString("term") != null) {
                googleAnalyticsParametersBuilder.term = googleAnalyticsParameters.getString("term")!!
            }

            if (googleAnalyticsParameters.getString("content") != null) {
                googleAnalyticsParametersBuilder.content = googleAnalyticsParameters.getString("content")!!
            }

            builder.setGoogleAnalyticsParameters(googleAnalyticsParametersBuilder.build())
        }
    }

    private fun buildIOSParameters(call: PluginCall, builder: DynamicLink.Builder) {
        val iosParameters = call.getObject("iosParameters")

        if (iosParameters.getString("bundleId") as String? != null) {
            val iosParameterBuilder = DynamicLink.IosParameters.Builder(iosParameters.getString("bundleId")!!)

            if (iosParameters.getString("appStoreId") != null) {
                iosParameterBuilder.appStoreId = iosParameters.getString("appStoreId")!!
            }

            if (iosParameters.getString("fallbackUrl") != null) {
                iosParameterBuilder.setFallbackUrl(Uri.parse(iosParameters.getString("fallbackUrl")))
            }

            if (iosParameters.getString("customScheme") != null) {
                iosParameterBuilder.customScheme = iosParameters.getString("customScheme")!!
            }

            if (iosParameters.getString("ipadFallbackUrl") != null) {
                iosParameterBuilder.ipadFallbackUrl = Uri.parse(iosParameters.getString("ipadFallbackUrl"))
            }

            if (iosParameters.getString("ipadBundleId") != null) {
                iosParameterBuilder.ipadBundleId = iosParameters.getString("ipadBundleId")!!
            }

            if (iosParameters.getString("minimumVersion") != null) {
                iosParameterBuilder.minimumVersion = iosParameters.getString("minimumVersion")!!
            }

            builder.setIosParameters(iosParameterBuilder.build())
        }
    }

    private fun buildAndroidParameters(call: PluginCall, builder: DynamicLink.Builder) {
        val androidParameters = call.getObject("androidParameters")
        if (androidParameters != null) {
            val androidParameterBuilder = DynamicLink.AndroidParameters.Builder()

            if (androidParameters.getInteger("minimumVersion") != null) {
                androidParameterBuilder.minimumVersion = androidParameters.getInteger("minimumVersion")!!
            }

            if (androidParameters.getString("fallbackUrl") != null) {
                androidParameterBuilder.fallbackUrl = Uri.parse(androidParameters.getString("fallbackUrl"))
            }

            builder.setAndroidParameters(androidParameterBuilder.build())
        }
    }

    private fun buildITunesParameters(call: PluginCall, builder: DynamicLink.Builder) {
        val iTunesConnectAnalyticsParameters = call.getObject("iTunesConnectAnalytics")
        if (iTunesConnectAnalyticsParameters != null) {
            val iTunesConnectAnalyticsParameterBuilder = DynamicLink.ItunesConnectAnalyticsParameters.Builder()

            if (iTunesConnectAnalyticsParameters.getString("providerToken") != null) {
                iTunesConnectAnalyticsParameterBuilder.providerToken = iTunesConnectAnalyticsParameters.getString("providerToken")!!
            }

            if (iTunesConnectAnalyticsParameters.getString("affiliateToken") != null) {
                iTunesConnectAnalyticsParameterBuilder.affiliateToken = iTunesConnectAnalyticsParameters.getString("affiliateToken")!!
            }

            if (iTunesConnectAnalyticsParameters.getString("campaignToken") != null) {
                iTunesConnectAnalyticsParameterBuilder.campaignToken = iTunesConnectAnalyticsParameters.getString("campaignToken")!!
            }

            builder.setItunesConnectAnalyticsParameters(iTunesConnectAnalyticsParameterBuilder.build())
        }
    }

    private fun buildSocialMetaParameters(call: PluginCall, builder: DynamicLink.Builder) {
        val socialMetaParameters = call.getObject("socialMeta")
        if (socialMetaParameters != null) {
            val socialMetaParameterBuilder = DynamicLink.SocialMetaTagParameters.Builder()

            if (socialMetaParameters.getString("title") != null) {
                socialMetaParameterBuilder.title = socialMetaParameters.getString("title")!!
            }

            if (socialMetaParameters.getString("description") != null) {
                socialMetaParameterBuilder.description = socialMetaParameters.getString("description")!!
            }

            if (socialMetaParameters.getString("imageUrl") != null) {
                socialMetaParameterBuilder.imageUrl = Uri.parse(socialMetaParameters.getString("imageUrl")!!)
            }

            builder.setSocialMetaTagParameters(socialMetaParameterBuilder.build())
        }
    }

    override fun handleOnNewIntent(intent: Intent) {
        super.handleOnNewIntent(intent)
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(activity) { pendingDynamicLinkData ->
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                    }
                    if (deepLink != null) {
                        val ret = JSObject()
                        
                        val intentData: Uri? = intent.data
                        if (intentData != null) {
                            val code: String = intentData.getQueryParameter("oobCode").orEmpty()
                            val mode: String = intentData.getQueryParameter("mode").orEmpty()
                            val parameter: String = intentData.toString()
                            ret.put("code", code)
                            ret.put("mode", mode)
                            ret.put("parameter", parameter)
                        }
                        
                        ret.put("url", deepLink.toString())
                        notifyListeners(EVENT_DEEP_LINK, ret, true)
                    }
                }
                .addOnFailureListener(activity) { e -> Log.e(logTag, "getDynamicLink:onFailure", e) }
    }

    companion object {
        private const val EVENT_DEEP_LINK = "deepLinkOpen"
    }
}
