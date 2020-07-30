from django.urls import re_path
from django.conf.urls import url

from . import consumers

websocket_urlpatterns = [
    #re_path
    #re_path(r'ws/chat/(?P<room_name>\w+)/$', consumers.ChatConsumer),
    re_path(r'ws/chat', consumers.ChatConsumer),
    #re_path(r'ws/group_chat', consumers.GroupChatConsumer),

]