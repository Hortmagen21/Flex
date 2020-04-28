from django.urls import path

from . import views
urlpatterns = [
    path('view_chat_room', views.view_chat_room),
    path('create_chat', views.create_chat),

]