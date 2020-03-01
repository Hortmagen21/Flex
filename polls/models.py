from django.db import models

# Create your models here.

class AccBase(models.Model):
    id = models.IntegerField(default=0)
    name = models.CharField(max_length=20,primary_key=True)
    email = models.CharField(max_length=35)
    password = models.IntegerField(default=0)