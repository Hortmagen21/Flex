from django.conf.urls import url,include
from django.urls import path
from . import views
urlpatterns = [
    path('follow', views.follow),
    path('check_i_follow', views.check_i_follow),
    path('followers', views.followers),
    path('view_acc', views.view_acc),
    path('add_post', views.add_post),
    path('view_photo', views.view_photo),
    path('like', views.like),
    path('comment', views.comment),
    path('view_post', views.view_post),
    path('view_all_posts', views.view_all_posts),
    path('unsubscribe', views.unsubscribe),
    path('dislike', views.dislike),
    path('avatar', views.ava),

]