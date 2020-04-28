# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models


class UserFollower(models.Model):
    id = models.IntegerField(primary_key=True)
    follower = models.IntegerField()

    class Meta:
        db_table = 'user_follower'
        unique_together = (('id', 'follower'),)


class PostBase(models.Model):
    id = models.AutoField(primary_key=True)
    user_id = models.IntegerField(blank=True, null=True)
    milliseconds = models.BigIntegerField()
    img = models.CharField(max_length=100)
    description = models.CharField(max_length=100, blank=True, null=True)
    img_mini = models.CharField(max_length=100)

    class Meta:
        ordering = ('milliseconds',)
        db_table = 'post_base'
        unique_together = (('id', 'img'),)


class Likes(models.Model):
    id_post = models.IntegerField(primary_key=True)
    id_user = models.IntegerField()

    class Meta:
        db_table = 'likes'
        unique_together = (('id_post', 'id_user'),)


class Comments(models.Model):
    id_post = models.IntegerField()
    id_user = models.IntegerField()
    comment = models.CharField(max_length=100)
    time = models.BigIntegerField()
    comment_id = models.AutoField(primary_key=True)
    class Meta:
        db_table = 'comments'
        unique_together = (('time', 'id_user'),)


class UserAvatar(models.Model):
    id_post = models.IntegerField(blank=True, null=True)
    id_user = models.IntegerField(primary_key=True)

    class Meta:
        db_table = 'user_avatar'
