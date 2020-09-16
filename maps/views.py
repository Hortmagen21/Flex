from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt, ensure_csrf_cookie, csrf_protect
from django.http import JsonResponse
from django.contrib.auth.models import User
from django.http import HttpResponse, HttpResponseRedirect, HttpResponseNotFound, HttpResponseBadRequest
from django.contrib.auth.decorators import login_required
from django.contrib.sessions.models import Session
from django.core.exceptions import MultipleObjectsReturned,ObjectDoesNotExist
core_url = 'https://sleepy-ocean-25130.herokuapp.com/'
# Create your views here.

@csrf_protect
@login_required(login_url=core_url+'acc_base/login_redirection')
def create_event(request):
    if request.method == 'POST':
        latitude = request.POST.get(['latitude'][0], False)
        longitude = request.POST.get(['longitude'][0], False)
        event_name = request.POST.get(['event_name'][0], False)
        time = request.POST.get(['time'][0], False)
        description = request.POST.get(['description'][0], False)
        img_src = request.POST.get(['img_src'][0], False)
        return HttpResponse(status=200)

    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)
