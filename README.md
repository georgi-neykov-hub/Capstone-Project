
Podcast Portal <img src="/app/src/main/res/mipmap-hdpi/ic_launcher.png" width="40"> 
============================================================================================

Podcast Portal is simple Material-styled podcast player for Android.

###Features

* Browsing  and searching through podcast resources
* Streaming online or playing local audio and video podcasts.
* Download episodes for offline use.
* Subscribing for content updates and new episodes

###Tech Details
* The app architecture is based on the wonderful [Nucleus](https://github.com/konmik/nucleus) MVP library
* Extensive use of [Dagger 2](http://google.github.io/dagger/) for resolving and injecting code dependencies.
* The Model and Presenter layers are implemented in a reactive, push-based style with [RxJava](https://github.com/ReactiveX/RxJava) streams.
* Data is backed by a a SQLite-backed ContentProvider
* Database queries are executed and observed with [SQLBrite](https://github.com/square/sqlbrite).
* The networking layer is based on the OkHTTP/Retrofit/Picasso stack.
* The player uses the support version of the MediaSession API.
* Support for devices running Android API17+ (Jelly Bean).

###Screenshots
<img src="/screenshots/explore.png" width="256">
<img src="/screenshots/explore2.png" width="256">
<img src="/screenshots/myPodcasts.png" width="256">
<img src="/screenshots/myPodcasts2.png" width="256">
<img src="/screenshots/player.png" width="256">
<img src="/screenshots/lockscreen.png" width="256">
<img src="/screenshots/settings.png" width="256">

<p>Sample video:<p/>
<a href="http://www.youtube.com/watch?feature=player_embedded&v=xmwxD_62Vdg
" target="_blank"><img src="http://img.youtube.com/vi/xmwxD_62Vdg/0.jpg" 
alt="Sample video" height="256" style="object-fit: cover; object-position: center;" border="10" /></a>
