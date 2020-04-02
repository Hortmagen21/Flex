from django.shortcuts import render
from user_profile.models import UserFollower, PostBase, Likes, Comments, UserAvatar
from django.http import HttpResponse, HttpResponseRedirect, HttpResponseNotFound, HttpResponseBadRequest
from django.contrib.auth.decorators import login_required
from django.contrib.sessions.models import Session
from django.core.exceptions import ObjectDoesNotExist
from django.db import DatabaseError
from django.views.decorators.csrf import csrf_exempt, ensure_csrf_cookie, csrf_protect
from django.http import JsonResponse
from urllib.parse import urlparse
import mimetypes
from django.utils.datastructures import MultiValueDictKeyError
from PIL import Image
from django.core.exceptions import MultipleObjectsReturned,ObjectDoesNotExist


import datetime
core_url = 'https://sleepy-ocean-25130.herokuapp.com/'
test_url = 'http://127.0.0.1:8000/'


@login_required(login_url=core_url+'acc_base/login_redirection')
def follow(request):
    if request.method == 'GET':
        user_follow = request.GET.get('id', ' ')
        user_id = int(request.session['_auth_user_id'])
        try:
            user = UserFollower(id=user_follow, follower=user_id)
        except DatabaseError:
            return HttpResponse('Duplicate follow', status=409)
        else:
            if user_follow != user_id:
                user.save()
                return HttpResponse('I follow')
            else:
                return HttpResponse('I can not follow myself',status=403)
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@login_required(login_url=core_url+'acc_base/login_redirection')
def check_i_follow(request):
    if request.method == 'GET':
        user_id = request.GET.get('id', int(request.session['_auth_user_id']))
        user_row = list(UserFollower.objects.filter(follower=int(user_id)))
        return HttpResponse(len(user_row))
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@login_required(login_url=core_url + 'acc_base/login_redirection')
def followers(request):
    if request.method == 'GET':
        user_id = request.GET.get('id', int(request.session['_auth_user_id']))
        user_row = list(UserFollower.objects.filter(id=int(user_id)))
        return HttpResponse(len(user_row))
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def add_post(request):
    if request.method == "POST":
        try:
            img = request.FILES['img']
        except MultiValueDictKeyError:
            response = HttpResponseNotFound()
        else:
            description = request.POST.get(['description'][0], '')
            user_id = int(request.session['_auth_user_id'])
            time = datetime.datetime.today()
            milliseconds = time.timestamp()*1000
            url = "user_profile/photos/{milliseconds}_{user_id}.jpg".format(user_id=user_id, milliseconds=milliseconds)
            url_mini = "user_profile/photos/{milliseconds}_{user_id}_mini.jpg".format(user_id=user_id, milliseconds=milliseconds)
            photo = PostBase(user_id=user_id, milliseconds=milliseconds, img=core_url + url, description=description, img_mini=core_url + url_mini)
            photo.save()
            with open(url, 'wb+') as destination:
                for chunk in img.chunks():
                    destination.write(chunk)
            im = Image.open(url)
            out = im.resize((384, 384))
            out.save(url_mini)
            response = JsonResponse({'src': core_url+url, 'src_mini': core_url+url_mini})
        return response
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_acc(request):
    if request.method == 'POST':
        user_id = request.POST.get(['id'][0], int(request.session['_auth_user_id']))
        posts_row = list(PostBase.objects.filter(user_id=user_id))
        posts = []
        for post in posts_row:
            posts.append({'src_mini': post.img_mini, 'date': post.milliseconds, 'description': post.description,
                          'post_id': post.id})
        return JsonResponse({'isMyUser': user_id, 'posts': posts}, content_type='application/json')
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_photo(request):
    if request.method == 'GET':
        img = request.GET.get('img', '')
        src = urlparse(img)
        try:
            print(src[2][1:])
            file = open(src[2][1:], 'rb+')
            mime_type_guess = mimetypes.guess_type(src[2][1:])
            print(mime_type_guess)
            if mime_type_guess is not None:
                response = HttpResponse(file, content_type=mime_type_guess[0])
            response['Content-Disposition'] = 'attachment; filename="{}"'.format(img)
        except IOError:
            response = HttpResponseNotFound()
        return response
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def like(request):
    if request.method == 'POST':
        user_id = int(request.session['_auth_user_id'])
        id_post = request.POST.get(['id'][0], False)
        if type(id_post) == bool:
            return HttpResponseBadRequest()
        feedback = Likes(id_post=int(id_post), id_user=int(user_id))
        feedback.save()
        return HttpResponse('liked')
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def comment(request):
    if request.method == 'POST':
        user_id = int(request.session['_auth_user_id'])
        id_post = request.POST.get(['id'][0], False)
        if type(id_post) == bool:
            return HttpResponseBadRequest()
        description = request.POST.get(['comment'][0], False)
        if type(description) == bool:
            return HttpResponseBadRequest()
        time = datetime.datetime.today()
        milliseconds = time.timestamp() * 1000
        feedback = Comments(id_post=int(id_post), id_user=int(user_id), comment=description, time=milliseconds)
        feedback.save()
        return HttpResponse('commented')
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_post(request):
    if request.method == 'GET':
        id_post = request.GET.get('id', '')
        try:
            post = PostBase.objects.get(id=int(id_post))
        except ObjectDoesNotExist:
            return HttpResponseNotFound()
        except MultipleObjectsReturned:
            return HttpResponseBadRequest()
        else:
            likes = list(Likes.objects.filter(id_post=int(id_post)))
            comments = list(Comments.objects.filter(id_post=int(id_post)))
            response = JsonResponse({'src': post.img, 'description': post.description, 'likes': len(likes), 'comments': len(comments)})
            return response
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)
