from django.shortcuts import render
from user_profile.models import UserFollower, PostBase, Likes, Comments, UserAvatar, AvaBase
from django.contrib.auth.models import User
from django.http import HttpResponse, HttpResponseRedirect, HttpResponseNotFound, HttpResponseBadRequest
from django.contrib.auth.decorators import login_required
from django.contrib.sessions.models import Session
from django.db import DatabaseError
from django.views.decorators.csrf import csrf_exempt, ensure_csrf_cookie, csrf_protect
from django.http import JsonResponse
from urllib.parse import urlparse
import mimetypes
from django.utils.datastructures import MultiValueDictKeyError
from PIL import Image
from django.core.exceptions import MultipleObjectsReturned,ObjectDoesNotExist
from fcm_django.api.rest_framework import FCMDevice
import datetime
import boto3
from urllib.parse import urlparse
core_url = 'https://sleepy-ocean-25130.herokuapp.com/'
test_url = 'http://127.0.0.1:8000/'


@csrf_protect
@login_required(login_url=core_url+'acc_base/login_redirection')
def follow(request):
    if request.method == 'GET':
        user_follow = request.GET.get('id', ' ')
        user_id = int(request.session['_auth_user_id'])
        try:
            user_test = UserFollower.objects.get(id=user_follow, follower=user_id)
        except ObjectDoesNotExist:
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
        except MultipleObjectsReturned:
            return HttpResponse('Duplicate follow', status=409)
        return HttpResponse(status=400)
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)

@csrf_protect
@login_required(login_url=core_url+'acc_base/login_redirection')
def username_list(request):
    if request.method == 'GET':
        id_list = request.GET.get('id_list', ' ').split()
        response_list = []
        for id in id_list:
            user = User.objects.get(id=int(id))
            try:
                ava = list(AvaBase.objects.filter(user_id=id))[-1]
            except ObjectDoesNotExist:
                return HttpResponse(status=404)
            except MultipleObjectsReturned:
                return HttpResponse(status=400)
            except IndexError:
                ava_src = 'None'
            else:
                #MINI
                ava_src = get_photo_url(time=ava.milliseconds, user_id_post=ava.user_id, img_name=ava.img_name)#ava_post.img_mini
            finally:
                response_list.append({"username": user.username, "ava_src": ava_src})
        return JsonResponse({"username_list": response_list})
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


from storages.backends.s3boto3 import S3Boto3Storage
from django.core.files.uploadedfile import InMemoryUploadedFile
import os


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def add_ava(request):
    if request.method == "POST":
        img = request.FILES['img']
        chat_id = request.POST.get(['chat_id'][0], False)
        user_id = int(request.session['_auth_user_id'])
        '''
        time = datetime.datetime.today()
        milliseconds = int(time.timestamp() * 1000)
        url = f'user_photo/{milliseconds}_{user_id}/'
        url_mini = f'user_photo/{milliseconds}_{user_id}/'
        clear_url = os.path.join(
            url,
            img.name
        )
        amazon_storage = S3Boto3Storage(bucket='flex-fox-21')
        print(img, type(img), 'description')
        if not amazon_storage.exists(url):
            amazon_storage.save(clear_url, img)
            file_url = amazon_storage.url(clear_url)
            if chat_id:
                photo = AvaBase(user_id=user_id, milliseconds=milliseconds,img_name=img.name, chat_id=chat_id)
            else:
                photo = AvaBase(user_id=user_id, milliseconds=milliseconds, img_name=img.name)
            photo.save()
            return JsonResponse({'src': file_url, 'src_mini': file_url, 'post_id': photo.id})
        else:
            file_name = img.name
            return JsonResponse({
                'message': f"Error: file {file_name} already exist at {url} in bucket"
            }, status=400)'''
        response = add_ava_local(img=img, chat_id=chat_id, user_id=user_id)
        return JsonResponse({'src': response['file_url'], 'src_mini': response['file_url'],
                             'post_id': response['photo_id']})
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def add_post(request):
    if request.method == "POST":
        img = request.FILES['img']
        description = request.POST.get(['description'][0], '')
        user_id = int(request.session['_auth_user_id'])
        time = datetime.datetime.today()
        milliseconds = int(time.timestamp() * 1000)
        url = f'user_photo/{milliseconds}_{user_id}/'
        url_mini = f'user_photo/{milliseconds}_{user_id}/'
        clear_url = os.path.join(
            url,
            img.name
        )
        amazon_storage = S3Boto3Storage(bucket=os.environ['S3_BUCKET_NAME'])
        print(img, type(img), 'description')
        if not amazon_storage.exists(url):
            amazon_storage.save(clear_url, img)
            file_url = amazon_storage.url(clear_url)
            photo = PostBase(user_id=user_id, milliseconds=milliseconds, img=file_url,
                                     description=description, img_mini=file_url, img_name=img.name)
            photo.save()
            return JsonResponse({'src': file_url, 'src_mini': file_url, 'post_id':photo.id})
        else:
            file_name = img.name
            return JsonResponse({
                'message': f"Error: file {file_name} already exist at {url} in bucket"
            }, status=400)
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


'''@csrf_protect
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
            im = Image.default_storage.open(url)
            out = im.resize((384, 384))
            out.save(url_mini)
            response = JsonResponse({'src': core_url+url, 'src_mini': core_url+url_mini})
        return response
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)
'''

@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_acc(request):
    if request.method == 'POST':
        user_id = request.POST.get(['id'][0], int(request.session['_auth_user_id']))
        avatars = list(AvaBase.objects.filter(user_id=user_id))
        posts_row = list(PostBase.objects.filter(user_id=user_id))
        try:
            user_name = User.objects.get(id=int(user_id)).username
        except ObjectDoesNotExist:
            return HttpResponseNotFound()
        except MultipleObjectsReturned:
            return HttpResponseBadRequest()
        posts = []
        avatar_array = []
        for post in posts_row:
            src_img = get_photo_url(time=post.milliseconds, user_id_post=post.user_id, img_name=post.img_name)
            posts.append({'src': src_img, 'src_mini': src_img, 'date': post.milliseconds, 'description': post.description,
                          'post_id': post.id})
        for avatar in avatars:
            avatar_array.append({'id_post': avatar.id})
        return JsonResponse({'isMyUser': user_id, 'user_name': user_name, 'posts': posts, 'ava': avatar_array, 'isSubscribed': isSubscribe(int(request.session['_auth_user_id']), int(user_id))}, content_type='application/json')
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


'''@csrf_protect
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
'''


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
        #avatars = list(UserAvatar.objects.filter(id_user=int(user_id)))
        user_followed = list(UserFollower.objects.filter(follower=int(user_id)))
        user_follower = list(UserFollower.objects.filter(id=int(user_id)))
        object_does_not_exist = False
        try:
            user_name = User.objects.get(id=int(user_id)).username
        except ObjectDoesNotExist:
            return HttpResponse(status=404)
        try:
            post = list(AvaBase.objects.filter(user_id=user_id))[-1]
        except ObjectDoesNotExist:
            object_does_not_exist = True
            ava_src = 'none'
        except IndexError:
            ava_src = 'none'
        else:
            ava_src = get_photo_url(time=post.milliseconds,user_id_post=post.user_id, img_name=post.img_name)
        finally:
            if object_does_not_exist:
                return HttpResponseNotFound()
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
            src_img = get_photo_url(time=post.milliseconds, user_id_post=post.user_id, img_name=post.img_name)
            posts.append({'src': src_img, 'date': post.milliseconds, 'description': post.description,
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
            multiple_follow = list(UserFollower.objects.filter(id=int(user_follow), follower=int(user_id)))
            for follow in multiple_follow:
                multiple_follow.delete()
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

from boto3.session import Session

@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def delete_post(request):
    if request.method == 'POST':
        post_id = request.POST.get(['post_id'][0], False)
        is_avatar = request.POST.get(['is_avatar'][0], False)
        user_id = int(request.session['_auth_user_id'])
        try:
            if is_avatar:
                img = AvaBase.objects.get(id=post_id, user_id=user_id)
            else:
                img = PostBase.objects.get(id=post_id, user_id=user_id)
        except ObjectDoesNotExist:
            return HttpResponse(status=404)
        else:
            time = img.milliseconds
            print(f'user_photo/{time}_{user_id}/'+img.img_name, 'URL')
            session = Session(aws_access_key_id=os.environ['S3_KEY'],
                              aws_secret_access_key=os.environ['S3_SECRET'])
            s3_resource = session.resource('s3')
            my_bucket = s3_resource.Bucket(os.environ['S3_BUCKET_NAME'])
            response = my_bucket.delete_objects(
                Delete={
                    'Objects': [
                        {
                            'Key': f'user_photo/{time}_{user_id}/'+img.img_name# the_name of_your_file
                        }
                    ]
                }
            )
            img.delete()
            return HttpResponse(status=200)
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
                follower_ava = list(AvaBase.objects.filter(user_id=int(follower.follower)))[-1]
                ava_post = get_photo_url(follower_ava.milliseconds,follower_ava.user_id, follower_ava.img_name)
            except IndexError:
                ava_post = 'None'
            except ObjectDoesNotExist:
                ava_post = 'None'#should change
            finally:
                response.append({'id': follower.follower, 'username': followed_user.username, 'ava_src': ava_post})
        return JsonResponse({"response": response})
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)

@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def test_fcm(request):
    if request.method == 'GET':
        user = int(request.session['_auth_user_id'])
        device = FCMDevice.objects.filter(device_id=user)
        device.send_message(data={"msg_id": 170, "ava": str('nONE')}, body='TEST')
        return HttpResponse("ok")


def add_ava_local(img, chat_id, user_id):
    time = datetime.datetime.today()
    milliseconds = int(time.timestamp() * 1000)
    url = f'user_photo/{milliseconds}_{user_id}/'
    url_mini = f'user_photo/{milliseconds}_{user_id}/'
    clear_url = os.path.join(
        url,
        img.name
    )
    amazon_storage = S3Boto3Storage(bucket='flex-fox-21')
    print(img, type(img), 'description')
    if not amazon_storage.exists(url):
        amazon_storage.save(clear_url, img)
        file_url = amazon_storage.url(clear_url)
        if chat_id:
            photo = AvaBase(user_id=user_id, milliseconds=milliseconds, img_name=img.name, chat_id=chat_id)
        else:
            photo = AvaBase(user_id=user_id, milliseconds=milliseconds, img_name=img.name)
        photo.save()
        return {'file_url': file_url, 'photo_id': photo.id}


def isSubscribe(my_id, user_id):
    try:
        follow = UserFollower.objects.get(id=user_id, follower=my_id)
    except ObjectDoesNotExist:
        return False
    except MultipleObjectsReturned:
        print('MULTIPLE FOLLOWS')
        return True
    else:
        return True


def isLiked(id_post, id_user):
    try:
        like = Likes.objects.get(id_post=id_post, id_user=id_user)
    except ObjectDoesNotExist:
        return False
    else:
        return True


def get_photo_url(time, user_id_post, img_name):
    amazon_storage = S3Boto3Storage(bucket='flex-fox-21')
    url = f'user_photo/{time}_{user_id_post}/'
    clear_url = os.path.join(
        url,
        img_name
    )
    file_url = amazon_storage.url(clear_url)
    return file_url
