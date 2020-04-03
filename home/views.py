from django.shortcuts import render
from django.contrib.auth.decorators import login_required
from django.views.decorators.csrf import csrf_exempt, ensure_csrf_cookie, csrf_protect
from user_profile.models import UserFollower, PostBase
from django.http import JsonResponse, HttpResponse, HttpResponseBadRequest
core_url = 'https://sleepy-ocean-25130.herokuapp.com/'
test_url = 'http://127.0.0.1:8000/'


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_home(request):
    if request.method == 'GET':
        last_id = request.GET.get('id', 0)
        user_id = int(request.session['_auth_user_id'])
        followed = list(UserFollower.objects.filter(follower=int(user_id)).values('id'))
        id_list = []
        for user in followed:
            id_list.append(user['id'])
        post = list(PostBase.objects.filter(user_id__in=id_list, id__gt=last_id).values('id')[:10])
        print(post)
        response = JsonResponse({"posts": post})
        return response
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)
