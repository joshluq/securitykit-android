package es.joshluq.securitykit.showcase

import android.app.Application
import es.joshluq.securitykit.manager.SecuritykitConfig
import es.joshluq.securitykit.manager.SecuritykitManager

class ShowcaseApp : Application() {

    lateinit var securityManager: SecuritykitManager

    override fun onCreate() {
        super.onCreate()
        
        // Initialize SecurityKit using the default EncryptionProvider (EncryptionKit)
        val config = SecuritykitConfig.build {
            context = this@ShowcaseApp
            storeName = "showcase_secure_store"
        }
        
        securityManager = SecuritykitManager.build(config)
    }
}