from django.shortcuts import render
from django.contrib.auth.decorators import login_required
from django.views.decorators.csrf import csrf_exempt, ensure_csrf_cookie, csrf_protect
from user_profile.models import UserFollower, PostBase, Likes, Comments
from django.http import JsonResponse, HttpResponse, HttpResponseBadRequest, HttpResponseNotFound
from django.core.exceptions import MultipleObjectsReturned, ObjectDoesNotExist
from user_profile.views import isLiked
import os
from user_profile.views import get_photo_url
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
        posts = list(PostBase.objects.filter(user_id__in=id_list, id__gt=last_id).values('id')[:10])
        information = []
        for id_of_post in posts:
            id_post = id_of_post['id']
            try:
                post = PostBase.objects.get(id=int(id_post))
            except ObjectDoesNotExist:
                return HttpResponseNotFound()
            except MultipleObjectsReturned:
                return HttpResponseBadRequest()
            else:
                time = post.milliseconds
                user_id_post = post.user_id
                file_url = get_photo_url(time=time, user_id_post=user_id_post, img_name=post.img_name)
                likes = list(Likes.objects.filter(id_post=int(id_post)))
                comments = list(Comments.objects.filter(id_post=int(id_post)))
                information.append({'src': file_url, 'src_mini': file_url, 'description': post.description,
                                    'likes': len(likes), 'comments': len(comments), 'id': post.id,
                                    "date": post.milliseconds,
                                    'isLiked': isLiked(int(id_post), int(request.session['_auth_user_id'])),
                                    'user_id': post.user_id})
        response = JsonResponse({"posts": information})
        return response
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)
