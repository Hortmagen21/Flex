from django.conf.urls import url,include
from django.urls import path
from . import views
urlpatterns = [
    path('follow', views.follow),
    path('check_i_follow', views.check_i_follow),
    path('followers', views.followers),
    path('view_acc', views.view_acc),
    path('add_post', views.add_post),
    #path('view_photo', views.view_photo),
    path('like', views.like),
    path('comment', views.comment),
    #path('view_post', views.view_post),
    path('view_all_posts', views.view_all_posts),
    path('unsubscribe', views.unsubscribe),
    path('dislike', views.dislike),
    #path('avatar', views.ava),
    path('view_all_comments', views.view_all_comments),
    path('view_information_user',views.view_information_user),
    path('view_subscribes',views.view_subscribes),#rewrite SQL #add try clause
    path('test_fcm',views.test_fcm),
    path('username_list', views.username_list),#rewrite SQL
    path('delete_post', views.delete_post),
    path('add_ava', views.add_ava),




]