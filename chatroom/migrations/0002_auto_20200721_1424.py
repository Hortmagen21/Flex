# Generated by Django 3.0.3 on 2020-07-21 11:24

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('chatroom', '0001_initial'),
    ]

    operations = [
        migrations.AlterModelOptions(
            name='message',
            options={'managed': False, 'ordering': ('-date',)},
        ),
        migrations.AddField(
            model_name='chat',
            name='is_group',
            field=models.BooleanField(blank=True, null=True),
        ),
    ]