# Generated by Django 3.0.3 on 2020-05-13 09:15

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='TokenConfirm',
            fields=[
                ('id', models.IntegerField(primary_key=True, serialize=False)),
                ('token', models.CharField(blank=True, max_length=100, null=True)),
            ],
            options={
                'db_table': 'token_confirm',
            },
        ),
    ]