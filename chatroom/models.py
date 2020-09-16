# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models


class Chat(models.Model):
    chat_id = models.AutoField(primary_key=True)
    chat_name = models.CharField(max_length=100, blank=True, null=True)
    chat_ava = models.CharField(max_length=100, blank=True, null=True)
    chat_admin = models.IntegerField()
    chat_members = models.IntegerField(blank=True, null=True)
    is_group = models.BooleanField(blank=True, null=True)

    class Meta:
        db_table = 'chat'


class ChatMembers(models.Model):
    ids = models.AutoField(primary_key=True)
    chat_id = models.IntegerField()
    user_id = models.IntegerField()

    class Meta:
        db_table = 'chat_members'
        unique_together = (('chat_id', 'user_id'),)


class Message(models.Model):
    chat_id = models.IntegerField()
    message = models.CharField(max_length=100, blank=True, null=True)
    message_id = models.AutoField(primary_key=True)
    user_id = models.IntegerField()
    date = models.IntegerField()

    class Meta:
        ordering = ('-date',)
        db_table = 'message'


class IgnoreMessages(models.Model):
    id_user = models.IntegerField(primary_key=True)
    id_message = models.IntegerField()

    class Meta:
        db_table = 'ignore_messages'
        unique_together = (('id_user', 'id_message'),)


class MsgType(models.Model):
    id = models.IntegerField(primary_key=True)
    type = models.CharField(max_length=50, blank=True, null=True)

    class Meta:
        db_table = 'msg_type'
