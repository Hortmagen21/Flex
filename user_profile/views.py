from django.shortcuts import render
from user_profile.models import UserFollower, PostBase, Likes, Comments, UserAvatar
from django.contrib.auth.models import User
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


@csrf_protect
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


@csrf_protect
@login_required(login_url=core_url+'acc_base/login_redirection')
def check_i_follow(request):
    if request.method == 'GET':
        user_id = request.GET.get('id', int(request.session['_auth_user_id']))
        user_row = list(UserFollower.objects.filter(follower=int(user_id)))

        return HttpResponse(len(user_row))
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@csrf_protect
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
            try:
                img = request.FILES['avatar']
            except MultiValueDictKeyError:
                HttpResponseBadRequest
            else:
                isAvatar = True
        else:
            isAvatar = False
        finally:
            description = request.POST.get(['description'][0], '')
            user_id = int(request.session['_auth_user_id'])
            time = datetime.datetime.today()
            milliseconds = time.timestamp()*1000
            url = f"user_profile/photos/{milliseconds}_{user_id}.jpg"
            url_mini = f"user_profile/photos/{milliseconds}_{user_id}_mini.jpg"
            if isAvatar:
                photo = PostBase(milliseconds=milliseconds, img=core_url + url, description=description, img_mini=core_url + url_mini)
            else:
                photo = PostBase(user_id=user_id, milliseconds=milliseconds, img=core_url + url, description=description, img_mini=core_url + url_mini)
            photo.save()
            if isAvatar:
                avatar = UserAvatar(id_post=int(photo.id), id_user=int(user_id))
                avatar.save()
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
        avatars = list(UserAvatar.objects.filter(id_user=user_id))
        posts_row = list(PostBase.objects.filter(user_id=user_id))
        try:
            user_name = User.objects.get(id=int(user_id)).username
        except ObjectDoesNotExist:
            return HttpResponseNotFound()
        except MultipleObjectsReturned:
            return HttpResponseBadRequest()
        posts = []
        avatars = []
        for post in posts_row:
            posts.append({'src': post.img, 'src_mini': post.img_mini, 'date': post.milliseconds, 'description': post.description,
                          'post_id': post.id})
        for avatar in avatars:
            avatars.append({'id_post': avatar.id_post})
        return JsonResponse({'isMyUser': user_id, 'user_name': user_name, 'posts': posts, 'ava': avatars, 'isSubscribed': isSubscribe(int(request.session['_auth_user_id']), int(user_id))}, content_type='application/json')
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
            response['Content-Disposition'] = f'attachment; filename="{img}"'
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
def view_post(request):#not using
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


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_information_user(request):
    if request.method == 'GET':
        user_id = request.GET.get('id', int(request.session['_auth_user_id']))
        avatars = list(UserAvatar.objects.filter(id_user=int(user_id)))
        user_followed = list(UserFollower.objects.filter(follower=int(user_id)))
        user_follower = list(UserFollower.objects.filter(id=int(user_id)))
        object_does_not_exist = False
        multiple_objects = False
        try:
            user_name = User.objects.get(id=int(user_id)).username
            post = PostBase.objects.get(id=int(avatars[-1].id_post))
        except ObjectDoesNotExist:
            object_does_not_exist=True
        except MultipleObjectsReturned:
            multiple_objects = True
        except IndexError:
            ava_src="none"
        else:
            ava_src = post.img
        finally:
            if object_does_not_exist:
                return HttpResponseNotFound()
            if multiple_objects:
                return HttpResponseBadRequest()
            return JsonResponse({'user_name': user_name, 'ava_src': ava_src, "followed": len(user_followed), "i_follower":len(user_follower), 'isSubscribed': isSubscribe(int(request.session['_auth_user_id']), int(user_id))})
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_all_posts(request):
    if request.method == 'POST':
        user_id = request.POST.get(['id'][0], int(request.session['_auth_user_id']))
        posts_row = list(PostBase.objects.filter(user_id=user_id))
        posts = []
        for post in posts_row:
            posts.append({'src': post.img, 'date': post.milliseconds, 'description': post.description,
                          'post_id': post.id, 'likes': len(list(Likes.objects.filter(id_post=int(post.id)))),
                          'comments': len(list(Comments.objects.filter(id_post=int(post.id)))),
                          'isLiked': isLiked(int(post.id), int(request.session['_auth_user_id']))})
        return JsonResponse({'isMyUser': user_id, 'posts': posts}, content_type='application/json')
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_all_comments(request):
    if request.method == 'GET':
        post_id = request.GET.get('id', ' ')
        comments_list = Comments.objects.filter(id_post=int(post_id))
        comments = []
        for comment in comments_list:
            comments.append({'comment_id': comment.comment_id,'sender_id': comment.id_user, 'description': comment.comment, 'time': comment.time})
        return JsonResponse({"comments": comments}, content_type='application/json')
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def unsubscribe(request):
    if request.method == 'GET':
        user_follow = request.GET.get('id', ' ')
        user_id = int(request.session['_auth_user_id'])
        try:
            connection = UserFollower.objects.get(id=int(user_follow), follower=int(user_id))
        except ObjectDoesNotExist:
            return HttpResponseNotFound()
        except MultipleObjectsReturned:
            return HttpResponseBadRequest()
        else:
            connection.delete()
            return HttpResponse('unfollowed')
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def ava(request):
    if request.method == 'GET':
        post_id = request.GET.get('id', ' ')
        user_id = int(request.session['_auth_user_id'])
        avatar = UserAvatar(id_post=int(post_id), id_user=int(user_id))
        avatar.save()
        return HttpResponse('ava created')
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def dislike(request):
    if request.method == 'POST':
        user_id = int(request.session['_auth_user_id'])
        id_post = request.POST.get(['id'][0], False)
        if type(id_post) == bool:
            return HttpResponseBadRequest()
        post = Likes.objects.get(id_post=int(id_post), id_user=int(user_id))
        post.delete()
        return HttpResponse('deleted')
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)

@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_subscribes(request):
    if request.method == 'GET':
        user_id = int(request.session['_auth_user_id'])
        followers = UserFollower.objects.filter(id=user_id)
        response = []
        for follower in followers:
            followed_user = User.objects.get(id=int(follower.follower))
            try:
                follower_ava = list(UserAvatar.objects.filter(id=int(follower.follower)))[-1]
                ava_post = (PostBase.objects.get(id=int(follower_ava.id_post))).img
            except IndexError:
                ava_post = 'None'
            finally:
                response.append({'id': follower.follower, 'username': followed_user.username, 'ava_src': ava_post})
        return JsonResponse(response)
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)

def isSubscribe(my_id, user_id):
    try:
        follow = UserFollower.objects.get(id=user_id, follower=my_id)
    except ObjectDoesNotExist:
        return False
    else:
        return True


def isLiked(id_post, id_user):
    try:
        like = Likes.objects.get(id_post=id_post, id_user=id_user)
    except ObjectDoesNotExist:
        return False
    else:
        return True
