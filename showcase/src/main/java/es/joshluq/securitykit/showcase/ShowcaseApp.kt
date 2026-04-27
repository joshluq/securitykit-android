package es.joshluq.securitykit.showcase

import android.app.Application
import es.joshluq.securitykit.sdk.SecurityKitConfig
import es.joshluq.securitykit.sdk.SecurityKit

class ShowcaseApp : Application() {

    lateinit var securityManager: SecurityKit

    override fun onCreate() {
        super.onCreate()
        
        // Initialize SecurityKit using the DSL with context parameter
        val config = SecurityKitConfig.build(this) {
            storeName = "showcase_secure_store"
        }
        
        securityManager = SecurityKit.Builder().build(config)
    }
}