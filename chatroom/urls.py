from django.urls import path

from . import views
urlpatterns = [
    path('view_chat_room', views.view_chat_room),
    path('create_chat', views.create_chat),
    path('upload_messages', views.upload_messages),#rewrite SQL
    path('create_group_chat', views.create_group_chat),#rewrite SQL
    path('get_chat_members', views.get_chat_members),
    path('add_to_group_chat', views.add_to_group_chat),
    path('remove_from_group_chat', views.remove_from_group_chat),
    path('follower_list_for_adding', views.follower_list_for_adding),


]