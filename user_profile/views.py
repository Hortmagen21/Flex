from django.shortcuts import render
from user_profile.models import UserFollowers
from django.http import HttpResponse,HttpResponseRedirect
from django.contrib.auth.decorators import login_required
from django.contrib.sessions.models import Session
from django.core.exceptions import ObjectDoesNotExist
core_url = 'https://sleepy-ocean-25130.herokuapp.com/'
test_url = 'http://127.0.0.1:8000/'


@login_required(login_url=core_url+'user_profile/login_redirection')
def follow(request):
    if request.method == 'GET':
        user_follow = request.GET.get('user_follow', ' ')
        user_id = int(request.session['_auth_user_id'])
        try:
            duplicate_user = UserFollowers.objects.get(id=user_follow)
        except ObjectDoesNotExist:
            user = UserFollowers(id=user_follow, followers=[])
            user.followers.append(user_id)
            user.save()
            return HttpResponse('i follow new user')
        else:
            duplicate_user.followers.append(user_id)
            duplicate_user.save()
            return HttpResponse('i follow')
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)
