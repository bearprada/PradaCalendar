package example.prada.lab.pradaoutlook.store

import android.content.Context

object EventStoreFactory {
    @JvmStatic fun getInstance(context: Context): IEventStore = ContentProviderEventStore.getInstance(context)
}
