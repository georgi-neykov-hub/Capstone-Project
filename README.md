Podcast Portal
==============

Podcast Portal is simple Material-styled podcast player for Android.

###Features

* Browsing  and searching through podcast resources
* Streaming online or playing local audio and video podcasts.
* Download episodes for offline use.
* Subscribing for content updates and new episodes

###Screenshots
<img src="/screenshots/explore.png" width="256">
<img src="/screenshots/explore2.png" width="256">
<img src="/screenshots/lockscreen.png" width="256">
<img src="/screenshots/myPodcasts.png" width="256">
<img src="/screenshots/myPodcasts2.png" width="256">
<img src="/screenshots/player.png" width="256">
<img src="/screenshots/settings.png" width="256">
<a href="http://www.youtube.com/watch?feature=player_embedded&v=xmwxD_62Vdg
" target="_blank"><img src="http://img.youtube.com/vi/xmwxD_62Vdg/0.jpg" 
alt="Sample video" width="512"  border="10" /></a>

###Tech Details
* The app architecture is based on the wonderful [Nucleus](https://github.com/konmik/nucleus) MVP library
* Extensive use of [Dagger 2](http://google.github.io/dagger/) for injecting code dependencies.
* The Model and Presenter layers are implemented in a reactive, push-based style by [RxJava](https://github.com/ReactiveX/RxJava) streams. Data is backed by a a SQLite-backed ContentProvider. Most if not all queries are executed and observed with [SQLBrite](https://github.com/square/sqlbrite).
* The networking layer is based on OkHTTP/Retrofit/Picasso stack.
* The player uses the support version of the MediaSession API.
* Support for devices running Android API17+ (Jelly Bean).
