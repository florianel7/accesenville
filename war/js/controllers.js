angular.module('starter.controllers', ['ngCookies'])

    .controller('AlertNewCtrl', function($scope,$ionicLoading,$filter,$ionicModal,$http,$ionicPopup,$cookieStore) {
        var $translate = function(input){
            return $filter("translate")(input);
        }
        $scope.newAlert = {};
        $scope.markerLat = 45.5;
        $scope.markerLgt = 9.2;
        $scope.buttonDisable = false;
        $scope.filename = new Date().getTime();
        $scope.acceptObj = {acceptAndContinue:false};
        $scope.uploadVisible = true;
        $scope.sendVisible = false;
        $scope.filename = null;


        $scope.setCoords = function(){
            if($cookieStore.get('coords') != null){
                var attrib = $cookieStore.get('coords');
                $scope.markerLat = attrib.markerLat;
                $scope.markerLgt = attrib.markerLgt;
                return attrib;
            }
            else{
                return {updatedCoord:false};
            }
        };

        $scope.coords = $scope.setCoords();

        $scope.setUserCookie = function(){
            if($cookieStore.get('userCookie') != null){
                console.log("user logged!");
                return $cookieStore.get('userCookie');
            }
            else{
                console.log("no user logged");
                return {};
            }
        };

        $scope.backToHome = function(){
            window.location.href = "#/tab/alert-new";
        }

        $scope.user = $scope.setUserCookie();

        $scope.userConnection = function(){
            $scope.data = {};

            $ionicPopup.show({
                templateUrl: 'templates/modal-login.html',
                title: $translate('enterEmail'),
                scope: $scope,
                buttons: [
                    { text: $translate('close'), onTap: function(e) { return true; } },
                    {
                        text: '<b>'+$translate('save')+'</b>',
                        type: 'button-dark',
                        onTap: function(e) {
                            return $scope.data.email;
                        }
                    }
                ]
            }).then(function(res) {
                console.log('Tapped!', res);
                $scope.controlExistence(res);
            }, function(err) {
                console.log('Err:', err);
            }, function(msg) {
                console.log('message:', msg);
            });
        }

        $scope.controlExistence = function(email){
            if(email != null){
                alert("control " + email);
            }
        }

        $scope.showAlert = function(value) {
            $ionicPopup.alert({
                title: $translate('Info'),
                content: value,
                okType: 'button-dark'
            }).then(function(res) {
                console.log('Close popup');
            });
        };

        $scope.saveAlert = function(){
            var dateNow = new Date();
            $scope.newAlert.creationDate = dateNow.getTime();
            $scope.newAlert.creationUser = $scope.user.email;
            $scope.newAlert.latitude = $scope.markerLat;
            $scope.newAlert.longitude = $scope.markerLgt;
            $scope.newAlert.state = "Start";
            $scope.newAlert.adminRisk = null; // admin opinion
            console.log("FILENAME " + $scope.filename);
            $scope.newAlert.photo = $scope.filename;

            $http({
                method : 'POST',
                url :   '/AlertServlet',
                data :  'method=' + 'NEW_ALERT' + '&'+
                    'newAlert=' + angular.toJson($scope.newAlert),
                headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
            }).success(function(data, status) {
                $scope.newAlert = {};
                $scope.backToHome();
                if('OK_BUT_CONFIRM_EMAIL' == data){
                    $scope.showAlert($translate('confirmationCode'));
                }
                else if('OK' == data){
                    $scope.showAlert($translate('createOk'));
                }
            });
        }

        $scope.createNewAlert = function(){
            $scope.buttonDisable = true;
            if($scope.newAlert.address != null
                && $scope.newAlert.address != ""){
                var textrequest = {
                    query: $scope.newAlert.address
                };

                console.log("createnew alert " + $scope.map);
                var service = new google.maps.places.PlacesService($scope.map);
                console.log("service initialize ok");
                service.textSearch(textrequest, textrequestcallback);
                console.log("after callback code...");

                function textrequestcallback(results, status) {
                    console.log("callback!");
                    if (status == google.maps.places.PlacesServiceStatus.OK) {
                        var place = results[0];
                        console.log("result " + results.length);
                        console.log("place " +JSON.stringify(place));
                        //console.log(place.geometry.location.lat()+" "+place.geometry.location.);
                        if(document.getElementById("map") != null){
                            $scope.updateMarkerPosition(place.geometry.location.lat(),place.geometry.location.lng());
                        }

                        $scope.markerLat = place.geometry.location.lat();
                        $scope.markerLgt = place.geometry.location.lng();

                        $scope.saveAlert();
                    }
                    else{
                        $scope.markerLat = 45.5;
                        $scope.markerLgt = 9.2;
                        $scope.newAlert.moreInformations = $translate('errorInputLocation');
                        console.log("error string input");
                        $scope.saveAlert();
                    }
                }
            }
            else{
                $scope.saveAlert();
            }
        };


        $ionicModal.fromTemplateUrl('templates/modal-privacy.html', {
            scope: $scope,
            animation: 'slide-in-up'
        }).then(function(modal) {
            $scope.modal = modal;
            console.log("done modal privacy");
        });

        $scope.openModalPrivacy = function(){
            $scope.modal.show();
        }

        $scope.openModal = function() {
            window.location.href = "#/tab/alert-create";
        };

        $scope.modalChanges = function() {
            console.log("INIT");
            console.log("title "+$scope.newAlert.title);
            console.log("desc "+$scope.newAlert.description);
            console.log("email "+$scope.user.email);
            console.log("accept "+$scope.acceptObj.acceptAndContinue);
            console.log("addre "+$scope.newAlert.address);
            console.log("addre "+$scope.markerLat +" " +$scope.markerLgt );
            console.log("updatedCoord " + $scope.coords.updatedCoord);
            if($scope.newAlert != null
                && $scope.newAlert.title != null
                && $scope.newAlert.title != ""
                && $scope.newAlert.description != null
                && $scope.newAlert.description != ""
                && $scope.user.email != null
                && $scope.user.email != ""
                && $scope.acceptObj.acceptAndContinue
                && ($scope.coords.updatedCoord || ($scope.newAlert.address != null && $scope.newAlert.address != ""))) {
                $scope.sendVisible = true;
            }
            else{
                $scope.sendVisible = false;
            }
        }

        $scope.closeModal = function() {
            $scope.modal.hide();
        };
        //Cleanup the modal when we're done with it!
        $scope.$on('$destroy', function() {
            $scope.modal.remove();
        });

        $scope.updateMarkerPosition = function(lat , lgt , zoom){
            var zoomValue = 16;
            if(zoom != null){
                zoomValue = zoom;
            }

            var posVar = new google.maps.LatLng(lat, lgt);
            var mapOptions = {
                center: posVar,
                zoom: zoomValue,
                zoomControl:false,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            };

            $scope.map = new google.maps.Map(document.getElementById("map"), mapOptions);

            $scope.map.controls[google.maps.ControlPosition.TOP_LEFT].push($scope.mySearchInput);
            var searchBox = new google.maps.places.SearchBox(($scope.mySearchInput));

            google.maps.event.addListener(searchBox, 'places_changed', function() {
                console.log("event places_changed!");
                var places = searchBox.getPlaces();
                if(places != null
                    && places.length > 0){
                    console.log("enter");
                    //map.setCenter(places[0].geometry.location);
                    console.log(places[0].geometry.location.lng());
                    console.log(places[0].geometry.location.lat());
                    $scope.updateMarkerPosition(places[0].geometry.location.lat(),places[0].geometry.location.lng(),null);
                }
            });

            $scope.map.setCenter(posVar);

            if($scope.marker != null){
                $scope.marker.setMap(null);
            }

            $scope.marker = new google.maps.Marker({
                map:$scope.map,
                draggable:true,
                animation: google.maps.Animation.DROP,
                position: posVar
            });

            google.maps.event.addListener($scope.marker,'dragend',
                function(event) {
                    $scope.markerLat = event.latLng.lat();
                    $scope.markerLgt = event.latLng.lng();
                    var coordsObj = {};
                    coordsObj.markerLat = event.latLng.lat();
                    coordsObj.markerLgt = event.latLng.lng();
                    coordsObj.updatedCoord = true;
                    $cookieStore.put('coords', coordsObj);
                    console.log( $scope.markerLat +" "+$scope.markerLgt);
                });


            $scope.markerLat = lat;
            $scope.markerLgt = lgt;

            var coordsObj = {};
            coordsObj.markerLat = lat;
            coordsObj.markerLgt = lgt;
            coordsObj.updatedCoord = true;
            $cookieStore.put('coords', coordsObj);

            console.log("UPDATE METHOD " +$scope.markerLat+" "+ $scope.markerLgt);

            if($scope.loading != null){
                $scope.loading.hide();
            }
        }

        $scope.centerOnMe = function() {
            if(!$scope.map) {
                return;
            }

            $scope.loading = $ionicLoading.show({
                content: $translate('getCurrentLocation'),
                showBackdrop: false
            });

            function getDefault(){
                console.log("from default");
                $cookieStore.put('latitude', 42);
                $cookieStore.put('longitude', 12);
                $scope.updateMarkerPosition(42,12,6);
            }

            /*function getCoordFromIpGeo(){
                var pos = geo.getPosition()
                if(pos != null){
                    console.log("from ip- geo");
                    $scope.updateMarkerPosition(pos.lat,pos.long);
                }
                else{
                    $scope.loading = $ionicLoading.show({
                        content: 'Error loading coordinates...',
                        showBackdrop: false
                    });
                }
            }*/

            if(!!navigator.geolocation){
                navigator.geolocation.getCurrentPosition(function(pos) {
                    console.log("from html5");
                    $scope.updateMarkerPosition(pos.coords.latitude,pos.coords.longitude,null);
                }, function(error) {
                    //getCoordFromIpGeo();
                    getDefault();
                }, {maximumAge:5000, timeout:6000, enableHighAccuracy:true});
            }
            else{
                //getCoordFromIpGeo();
                getDefault();
            }
        };

        $scope.initialize = function() {
            console.log("INITIALIZE MAP");
            $scope.mySearchInput = document.getElementById('pac-input-mypos');

            if(document.getElementById("map") != null){
                var mapOptions = {
                    center: new google.maps.LatLng($scope.markerLat,$scope.markerLgt),
                    zoom: 6,
                    zoomControl:true,
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                };
                var map = new google.maps.Map(document.getElementById("map"),
                    mapOptions);

                map.controls[google.maps.ControlPosition.TOP_LEFT].push($scope.mySearchInput);
                var searchBox = new google.maps.places.SearchBox(($scope.mySearchInput));

                google.maps.event.addListener(searchBox, 'places_changed', function() {
                    var places = searchBox.getPlaces();
                    if(places != null
                        && places.length > 0){
                        $scope.updateMarkerPosition(places[0].geometry.location.lat(),places[0].geometry.location.lng());
                    }
                });

                $scope.map = map;
            }
            else{
                console.log("first time");
            }
        };

        google.maps.event.addDomListener(window, 'load', $scope.initialize());

        $scope.fileUpload = function(){
            console.log("clicked");
            //global variables for uploading files
            var my_bucket = null;
            var my_policy = null;
            var my_GoogleAccessId = null;
            var my_signature = null;
            var my_gcs_url = null;
            var my_key = null;
            var my_key_gcs=null;

            $(function () {
                $('#fileupload').fileupload({
                    //dataType: 'text/plain', //need to have this content type in order to prevent errors when using iFrame upload
                    //type: 'POST',
                    forceIframeTransport: true, //need to use this modality because IE8/9 does not work with XHR
                    add: function (e, data) {
                        $('#loadingIcon').show();
                        $scope.filename = new Date().getTime();
                        $.ajax({
                            type:'POST',
                            url:'/GcsServlet',
                            async:false,
                            success: function(data){
                                my_bucket=data.bucket;
                                my_policy=data.policy;
                                my_GoogleAccessId=data.googleAccessID;
                                my_signature=data.signature;
                                my_gcs_url=data.url;

                                console.log("callback from gcsservlet");
                                $scope.buttonDisable = true;
                                $scope.loading = $ionicLoading.show({
                                    content: $translate('pleaseWait'),
                                    showBackdrop: false
                                });
                            }
                        });
                        //get file name
                        my_key= $scope.filename;
                        //set file upload options
                        $('#fileupload').fileupload(
                            'option',
                            {
                                url:my_gcs_url,

                                formData:[
                                    {
                                        name:'key',
                                        value:my_key

                                    },
                                    {
                                        name:'bucket',
                                        value:my_bucket

                                    },
                                    {
                                        name:'policy',
                                        value:my_policy

                                    },
                                    {
                                        name:'GoogleAccessId',
                                        value:my_GoogleAccessId

                                    },
                                    {
                                        name:'success_Action_status',
                                        value: '201'
                                    },
                                    {
                                        name:'signature',
                                        value:my_signature

                                    }
                                ]
                            }
                        );
                        data.submit();
                    },
                    //success callback
                    success:function(e,data){
                        //check if the file has been uploaded correctly into GCS
                        $.ajax({
                            type:'POST',
                            url:'/CheckObjExistence',
                            data: {'inputFile':my_key},
                            async:false,
                            success: function(data){
                                $scope.showAlert($translate('uploadOk'));
                                $scope.uploadVisible = false;
                                $scope.loading.hide();
                                $scope.buttonDisable = false;
                                $('#loadingIcon').hide();
                            }
                        });
                    },
                    progressall: function (e, data) {
                        var progress = parseInt(data.loaded / data.total * 100, 10);
                        console.log(progress);
                        $('#progressxx .barxx').css(
                            'width',
                                progress + '%'
                        );
                    }
                })
            });

        };
    })

    .controller('AlertListCtrl', function($scope, $http, $location,$filter, $ionicPopup, $cookieStore, $ionicLoading) {
        $scope.user = $cookieStore.get('userCookie');

        var $translate = function(input){
            return $filter("translate")(input);
        }

        $scope.search = {
            filter:''
        }

        $scope.openSearchModal = function(){
            var searchPopUp = $ionicPopup.show({
                templateUrl: 'templates/popup-template.html',
                title: $translate('enterSearchFilter'),
                scope: $scope,
                buttons: [
                    { text:  $translate('cancel') , onTap: function(e) { return true; } },
                    {
                        text: '<b>'+$translate('search')+'</b>',
                        type: 'button-dark',
                        onTap: function(e) {
                            console.log($scope.search.filter);
                            return true;
                        }
                    }
                ]
            });
            searchPopUp.then(function(res) {
                console.log('Tapped!', res);
            });
        }


        $scope.getListAllViewAlert = function(){
            $scope.loading = $ionicLoading.show({
                content: $translate('loadingAlertInProgress'),
                showBackdrop: false
            });
            $http({
                method : 'POST',
                url :   '/AlertServlet',
                data :  'method=' + 'LIST_ALERT' + '&'+
                        'user='+angular.toJson($scope.user),
                headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
            }).success(function(data, status) {
                console.log("back from backend");
                $scope.alertList = data;
                window.location.href = "#/tab/alert-list";
                if($scope.loading != null){
                    $scope.loading.hide();
                }
            });
        }

        $scope.goToListEarth = function(){
            window.location.href = "#/tab/alert-list-earth";
        }

        $scope.getListAllViewAlertEarthMode = function(latitude,longitude,zoom){

            if($scope.loading != null){
                $scope.loading.hide();
            }

            $scope.loading = $ionicLoading.show({
                content: $translate('loadingAlertInProgress'),
                showBackdrop: false
            });

            $http({
                method : 'POST',
                url :   '/AlertServlet',
                data :  'method=' + 'LIST_ALERT' + '&'+
                        'user='+angular.toJson($scope.user),
                headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
            }).success(function(data, status) {
                console.log("back from backend");
                $scope.alertList = data;
                $scope.drawGlobalMap(latitude,longitude,zoom);
            });
        }

        $scope.drawGlobalMap = function(latitude,longitude,zoom){
            var zoomValue = 12;
            if(zoom != null){
                zoomValue = zoom;
            }

            $scope.globalSearchInput = document.getElementById('pac-input');
            $scope.globalMap;
            $scope.globalLatLng = new google.maps.LatLng(latitude,longitude);
            $scope.globalMapOptions;
            $scope.globalMarkerList;
            $scope.globalSearchInput;

            $scope.globalMapOptions = {
                center: $scope.globalLatLng,
                zoom: zoomValue,
                zoomControl:false,
                mapTypeId: google.maps.MapTypeId.ROAD
            };

            $scope.globalMap = new google.maps.Map(document.getElementById("map-earth"),
                $scope.globalMapOptions);

            $scope.globalMap.controls[google.maps.ControlPosition.TOP_LEFT].push($scope.globalSearchInput);
            var searchBox = new google.maps.places.SearchBox(($scope.globalSearchInput));

            google.maps.event.addListener(searchBox, 'places_changed', function() {
                var places = searchBox.getPlaces();
                if(places != null
                    && places.length > 0){
                    $scope.globalMap.setCenter(places[0].geometry.location);
                }
            });

            $scope.setMarkerListIntoMap();

            $scope.globalMap.setCenter($scope.globalLatLng);
        }

        $scope.centerTotaAlertListOnMyPosition = function() {
            $scope.loading = $ionicLoading.show({
                content: $translate('getCurrentLocation'),
                showBackdrop: false
            });

            /*function getCoordFromIpGeo(){
                var pos = geo.getPosition()
                if(pos != null){
                    console.log("from ip- geo");
                    $cookieStore.put('latitude', pos.lat);
                    $cookieStore.put('longitude', pos.long);
                    $scope.getListAllViewAlertEarthMode(pos.lat,pos.long);
                }
                else{
                    $scope.loading = $ionicLoading.show({
                        content: 'Error loading coordinates...',
                        showBackdrop: false
                    });
                }
            }*/

            console.log("get position object");
            console.log("lat " + $cookieStore.get('latitude'));
            console.log("lgt " + $cookieStore.get('longitude'));

            function getTotalDefault(){
                console.log("from default TOTAL");
                $cookieStore.put('latitude', 42);
                $cookieStore.put('longitude', 12);
                $cookieStore.put('zoom', 6);
                $scope.getListAllViewAlertEarthMode(41,12,6);
            }

            if($cookieStore.get('latitude') == null
                || $cookieStore.get('longitude') == null ){
                if(!!navigator.geolocation){
                    navigator.geolocation.getCurrentPosition(function(pos) {
                        console.log("from html5");
                        $cookieStore.put('latitude', pos.coords.latitude);
                        $cookieStore.put('longitude', pos.coords.longitude);
                        $cookieStore.put('zoom', 12);
                        $scope.getListAllViewAlertEarthMode(pos.coords.latitude,pos.coords.longitude,12);
                    }, function(error) {
                        //getCoordFromIpGeo();
                        getTotalDefault();
                    }, {maximumAge:5000, timeout:6000, enableHighAccuracy:true});
                }
                else{
                    //getCoordFromIpGeo();
                    getTotalDefault();
                }
            }
            else{
                console.log("locatization from cache");
                $scope.getListAllViewAlertEarthMode($cookieStore.get('latitude'),$cookieStore.get('longitude'),$cookieStore.get('zoom'));
            }
        };

        $scope.setMarkerListIntoMap = function(){
            var imageUrl = 'img/marker-icon.png';

            //at first reset the array of marker and clean the map
            if($scope.globalMarkerList != null
                && $scope.globalMarkerList.length > 0){
                angular.forEach($scope.globalMarkerList, function(markerObj) {
                    markerObj.setMap(null);
                });
            }

            $scope.globalMarkerList = [];

            angular.forEach($scope.alertList, function(alertGlobalObj) {

                if(alertGlobalObj.adminRisk != null){
                    if(alertGlobalObj.adminRisk == 'barrieraPubblicata'){
                        imageUrl = 'img/rosso.png';
                    }
                    else if(alertGlobalObj.adminRisk == 'barrieraDenunciata'
                            || alertGlobalObj.adminRisk == 'barrieraSegnalata'){
                        imageUrl = 'img/blu.png';
                    }
                    else if(alertGlobalObj.adminRisk == 'pronunciamentoGiudiziario'){
                        imageUrl = 'img/viola.png';
                    }
                    else if(alertGlobalObj.adminRisk == 'barrieraRimossa'){
                        imageUrl = 'img/verde.png';
                    }
                    else{
                        imageUrl = 'img/marker-icon.png';
                    }
                }
                else{
                    imageUrl = 'img/rosso.png';
                }

                var image = {
                    url: imageUrl,
                    origin: new google.maps.Point(0,0),
                    anchor: new google.maps.Point(0, 32)
                };

                var tempMarker = new google.maps.Marker({
                    position: new google.maps.LatLng(alertGlobalObj.latitude,alertGlobalObj.longitude),
                    map: $scope.globalMap,
                    draggable:true,
                    animation: google.maps.Animation.DROP,
                    title:$translate(alertGlobalObj.adminRisk),
                    icon: image,
                    maxWidth: 200,
                    maxHeight: 200
                });

                $scope.globalMarkerList[$scope.globalMarkerList.length] = tempMarker;

                google.maps.event.addListener(tempMarker, 'click', function() {
                	if(alertGlobalObj.photo != null){
                		$scope.getFilePreviewInformations(tempMarker,alertGlobalObj);
                	}
                	else{
                		$scope.fileLink = "no-image";
                        $scope.fileLinkAfter = "no-image";
                		$scope.createInfoWindow(tempMarker,alertGlobalObj);
                	}
                });
            });

            if($scope.loading != null){
                $scope.loading.hide();
            }
        }

        $scope.showAlert = function(value) {
            $ionicPopup.alert({
                title: $translate('Info'),
                content: value,
                okType: 'button-dark'
            }).then(function(res) {
                console.log('Close popup');
            });
        };

        $scope.getFilePreviewInformations = function(tempMarker,alertGlobalObj){
            $http({
                method : 'POST',
                url :   '/UploadFileServlet',
                data :  'method=' + 'GET_FILE_LINK_BY_ALERT' + '&'+
                    'fileName=' + alertGlobalObj.photo ,
                headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
            }).success(function(data, status) {
                if('ERROR' == data){
                    $scope.showAlert($translate('error'));
                }
                else{
                    $scope.fileLink = data;
                    if(alertGlobalObj.photoAfter != null){
                        $scope.getFileAfterPreviewInformations(tempMarker,alertGlobalObj);
                    }
                    else{
                        $scope.createInfoWindow(tempMarker,alertGlobalObj);
                    }
                }
            });
        };

        $scope.getFileAfterPreviewInformations = function(tempMarker,alertGlobalObj){
            $http({
                method : 'POST',
                url :   '/UploadFileServlet',
                data :  'method=' + 'GET_FILE_LINK_BY_ALERT' + '&'+
                    'fileName=' + alertGlobalObj.photoAfter ,
                headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
            }).success(function(data, status) {
                if('ERROR' == data){
                    $scope.showAlert($translate('error'));
                }
                else{
                    $scope.fileLinkAfter = data;
                    $scope.createInfoWindow(tempMarker,alertGlobalObj);
                }
            });
        };

        $scope.stringCutter = function(text , length){
            if(!isNaN(length)
                && length > 0
                && text.length > length){
                return text.substring(0,length)+"...";
            }
            return text;
        };

        $scope.totalInfoWindowList = [];

        $scope.createInfoWindow = function(tempMarker,alertGlobalObj){
            var fileLink = "";
            var arrayLink = "";
            var arrayLinkAfter = "";
            var imageToShow = "img/no_image.jpg";
            var imageToShowAfter = "img/no_image.jpg";
            if($scope.fileLink != "no-image"){
                imageToShow = $scope.fileLink;
                arrayLink = '<a href="'+imageToShow+'" target="_blank">'+$translate('directLink')+'</a>';
            }

            console.log("link after");
            console.log($scope.fileLinkAfter);
            if($scope.fileLinkAfter != null
                && $scope.fileLinkAfter != "no-image"
                && $scope.fileLinkAfter.indexOf('no-image') < 0){
                console.log("link after");
                console.log($scope.fileLinkAfter);
                imageToShowAfter = $scope.fileLinkAfter;
                arrayLinkAfter = '<br><a href="'+imageToShowAfter+'" target="_blank">'+$translate('photoAfter')+'</a>';
            }

            var contentString = '<table width="180px" style="overflow-x:hidden;overflow-y:auto;">'+
                '<tr>'+
                '<th><img src="'+imageToShow+'" style="width:140px;"><br>' +
                '<a href="'+'#/tab/alert-detail/'+alertGlobalObj.id+'" >'+$translate('moreDetails')+'</a><br>'+
                arrayLink+
                arrayLinkAfter+
                '</th>'+
                '</table>';

            var tempInfoWindow = new google.maps.InfoWindow({
                content: contentString
            });


            if($scope.totalInfoWindowList != null
                && $scope.totalInfoWindowList.length > 0){
                angular.forEach($scope.totalInfoWindowList, function(currentInfoWindowObj) {
                    currentInfoWindowObj.close();
                });
            }
            tempInfoWindow.open($scope.globalMap,tempMarker);
            $scope.totalInfoWindowList.push(tempInfoWindow);
        }
    })

    .controller('AlertDetailCtrl', function($scope, $stateParams, $filter,$http, $cookieStore, $ionicPopup, $ionicLoading) {
        $scope.alert = {};
        $scope.message = {};
        $scope.obj = {};
        $scope.isPhotoAfter = false;

        var $translate = function(input){
            return $filter("translate")(input);
        }

        $scope.changePhotoAfter = function(){
            $scope.isPhotoAfter = !$scope.isPhotoAfter;
            console.log($scope.isPhotoAfter);
        }

        $scope.fileUploadEditMode = function(){
            console.log("clicked");
            //global variables for uploading files
            var my_bucket = null;
            var my_policy = null;
            var my_GoogleAccessId = null;
            var my_signature = null;
            var my_gcs_url = null;
            var my_key = null;
            var my_key_gcs=null;

            $(function () {
                $('#fileupload').fileupload({
                    //dataType: 'text/plain', //need to have this content type in order to prevent errors when using iFrame upload
                    //type: 'POST',
                    forceIframeTransport: true, //need to use this modality because IE8/9 does not work with XHR
                    add: function (e, data) {
                        $('#loadingIcon').show();
                        $scope.filename = new Date().getTime();
                        $.ajax({
                            type:'POST',
                            url:'/GcsServlet',
                            async:false,
                            success: function(data){
                                my_bucket=data.bucket;
                                my_policy=data.policy;
                                my_GoogleAccessId=data.googleAccessID;
                                my_signature=data.signature;
                                my_gcs_url=data.url;

                                console.log("callback from gcsservlet");
                                $scope.buttonDisable = true;
                                $scope.loading = $ionicLoading.show({
                                    content: $translate('pleaseWait'),
                                    showBackdrop: false
                                });
                            }
                        });
                        //get file name
                        my_key= $scope.filename;
                        //set file upload options
                        $('#fileupload').fileupload(
                            'option',
                            {
                                url:my_gcs_url,

                                formData:[
                                    {
                                        name:'key',
                                        value:my_key

                                    },
                                    {
                                        name:'bucket',
                                        value:my_bucket

                                    },
                                    {
                                        name:'policy',
                                        value:my_policy

                                    },
                                    {
                                        name:'GoogleAccessId',
                                        value:my_GoogleAccessId

                                    },
                                    {
                                        name:'success_Action_status',
                                        value: '201'
                                    },
                                    {
                                        name:'signature',
                                        value:my_signature

                                    }
                                ]
                            }
                        );
                        data.submit();
                    },
                    //success callback
                    success:function(e,data){
                        //check if the file has been uploaded correctly into GCS
                        $.ajax({
                            type:'POST',
                            url:'/CheckObjExistence',
                            data: {'inputFile':my_key},
                            async:false,
                            success: function(data){
                                $scope.loading.hide();
                                $('#loadingIcon').hide();
                                if(!$scope.isPhotoAfter){
                                    $scope.alert.photo = $scope.filename;
                                }
                                else {
                                    $scope.alert.photoAfter = $scope.filename;
                                }
                                $scope.updateAlert();
                            }
                        });
                    },
                    progressall: function (e, data) {
                        var progress = parseInt(data.loaded / data.total * 100, 10);
                        console.log(progress);
                        $('#progressxx .barxx').css(
                            'width',
                                progress + '%'
                        );
                    }
                })
            });

        };

        $scope.editAlertVisibility = function(){
            console.log($scope.alert.enabled);
            $scope.updateAlert();
        }

        $scope.getFileLink = function(photo){
            console.log("PHOTO NAME " + photo);
            $http({
                method : 'POST',
                url :   '/UploadFileServlet',
                data :  'method=' + 'GET_FILE_LINK_BY_ALERT' + '&'+
                    'fileName=' + photo ,
                headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
            }).success(function(data, status) {
                if('ERROR' == data){
                    $scope.showAlert($translate('error'));
                }
                else{
                    $scope.linkVisible = true;
                    $scope.photoLink = data;
                }
            });
        };

        $scope.getFileAfterLink = function(photo){
            console.log("PHOTO NAME AFTER" + photo);
            $http({
                method : 'POST',
                url :   '/UploadFileServlet',
                data :  'method=' + 'GET_FILE_LINK_BY_ALERT' + '&'+
                    'fileName=' + photo ,
                headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
            }).success(function(data, status) {
                if('ERROR' == data){
                    $scope.showAlert($translate('error'));
                }
                else{
                    $scope.linkAfterVisible = true;
                    $scope.photoAfterLink = data;
                }
            });
        };

        $scope.sendEmail = function(){
            $http({
                method : 'POST',
                url :   '/AlertServlet',
                data :  'method=' + 'SEND_EMAIL' + '&'+
                        'message=' + $scope.message.messageText + '&'+
                        'sendEmail=' + $scope.message.checkedSendEmail + '&'+
                        'client=' + $scope.alert.creationUser+ '&'+
                        'alertId=' + $scope.alert.id,
                headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
            }).success(function(data, status) {
                if('OK' == data){
                    $scope.showAlert($translate('emailOk'));
                }
            });
        }

        $scope.user = $cookieStore.get('userCookie');

        $scope.changeRealLevel = function(value){
            $scope.alert.adminRisk = value;
            $scope.updateAlert();
        }

        $scope.changeStatus = function(value){
            $scope.alert.state = value;
            $scope.updateAlert();
        }

        $scope.showAlert = function(value) {
            $ionicPopup.alert({
                title: $translate('Info'),
                content: value,
                okType: 'button-dark'
            }).then(function(res) {
                console.log('Close popup');
            });
        };

        $scope.updateAlertAddress = function(){
            $http({
                method : 'POST',
                url :   '/AlertServlet',
                data :  'method=' + 'EDIT_ALERT_ADDRESS' + '&'+
                    'newAlert=' + angular.toJson($scope.alert),
                headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
            }).success(function(data, status) {
                if('OK' == data){
                    $scope.showAlert($translate('updateOk'));
                }
            });
        }

        $scope.updateAlert = function(){
            $http({
                method : 'POST',
                url :   '/AlertServlet',
                data :  'method=' + 'EDIT_ALERT' + '&'+
                    'newAlert=' + angular.toJson($scope.alert),
                headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
            }).success(function(data, status) {
                if('OK' == data){
                    $scope.showAlert($translate('updateOk'));
                }
            });
        }

        $scope.control = function(){
            if($scope.user != null){
                return $scope.user.type == 'Admin';
            }
            else{
                return false;
            }
        }

        $scope.goToEdit = function(alertId){
            window.location.href = "#/tab/alert-edit/"+alertId;
        }

        $scope.backToList = function(){
            window.location.href = "#/tab/alert-list";
        }

        $scope.initializeMapDetail = function(lat , lgt) {
            console.log("create map detail");
            var mapOptions = {
                center: new google.maps.LatLng(lat,lgt),
                zoom: 18,
                mapTypeId: google.maps.MapTypeId.SATELLITE,
                panControl: false,
                zoomControl: false,
                disableDefaultUI: true
            };
            var map = new google.maps.Map(document.getElementById("map-detail"), mapOptions);

            // Stop the side bar from dragging when mousedown/tapdown on the map
            google.maps.event.addDomListener(document.getElementById('map-detail'), 'mousedown', function(e) {
                e.preventDefault();
                return false;
            });
        };

        $scope.viewAllMessage = function(message){
            $cookieStore.put('fullMessage', message);
            console.log(message);
            window.location.href = "#/tab/alert-detail-full-message";
        }

        $scope.getFullMessage = function(){
        	console.log("init get full message");
        	console.log($cookieStore.get('fullMessage'));
            $scope.fullMessage = $cookieStore.get('fullMessage');
        }

        $scope.getMessagesByAlert = function(alertId){
            $scope.showMessageList = false;
            $http({
                method : 'POST',
                url :   '/AlertServlet',
                data :  'method=' + 'GET_MESSAGES_BY_ALERT'+ '&'+
                        'alertId=' + alertId,
                headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
            }).success(function(data, status) {
                if(data != null
                    && data.length > 0){
                    $scope.showMessageList = true;
                    $scope.messageList = data;
                }
                else{
                    $scope.showMessageList = false;
                }
            });
        }

        $scope.init = function(value){
            $scope.isPhotoAfter = false;
            console.log("init detail alert");
            $http({
                method : 'POST',
                url :   '/AlertServlet',
                data :  'method=' + 'GET_ALERT'+ '&'+
                        'alertId=' + $stateParams.alertId,
                headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
            }).success(function(data, status) {
                $scope.alert = data;
                if($scope.alert != null){
                    if(value != 'editMode'){
                        $scope.initializeMapDetail($scope.alert.latitude,$scope.alert.longitude);
                        $scope.getMessagesByAlert($scope.alert.id);
                    }
                    $scope.linkVisible = false;
                    $scope.linkAfterVisible = false;
                    if($scope.alert.photo != null){
                    	$scope.getFileLink($scope.alert.photo);
                    }
                    if($scope.alert.photoAfter != null){
                        $scope.getFileAfterLink($scope.alert.photoAfter);
                    }
                }
            });
        }
    })

    .controller('AuthCtrl', function($scope, $http,$filter, $ionicPopup, $cookieStore) {
        var $translate = function(input){
            return $filter("translate")(input);
        }

        $scope.logout = function(){
            $cookieStore.remove('userCookie');
            $scope.showAlert($translate('bye'));
            window.location.href = "#/tab/alert-new";
        }

        $scope.showLogin = true;

        $scope.setUserCookie = function(){
            if($cookieStore.get('userCookie') != null){
                console.log("user logged!");
                $scope.showLogin = false;
                return $cookieStore.get('userCookie');
            }
            else{
                $scope.showLogin = true;
                console.log("no user logged");
                return {};
            }
        };

        $scope.user = $scope.setUserCookie();

        $scope.showAlert = function(value) {
            $ionicPopup.alert({
                title: $translate('Info'),
                content: value,
                okType: 'button-dark'
            }).then(function(res) {
                console.log('Close popup');
            });
        };

        $scope.registerNewUser = function(){
            if($scope.user.name != null
                && $scope.user.surname != null
                && $scope.user.email != null
                && $scope.user.password != null
                && $scope.user.password == $scope.user.repeatpassword){
                $scope.user.type = "Default";
                $scope.user.active = false;

                $http({
                    method : 'POST',
                    url :   '/AuthenticationServlet',
                    data :  'method=' + 'NEW_REGISTRATION' + '&'+
                        'user=' + angular.toJson($scope.user),
                    headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
                }).success(function(data, status) {
                    $scope.loadingRegistration = false;
                    if('OK' == data){
                        $scope.showAlert($translate('registrationCorrect'));
                    }
                    else if('ERROR' == data){
                        $scope.showAlert($translate('registrationProblems'));
                    }
                    else if('ALREADY_EXISTS' == data){
                        $scope.showAlert($translate('registrationClone'));
                    }
                });
            }
            else{
                $scope.showAlert($translate('insertCorrectly'));
            }
        }

        $scope.setUserCookies = function(data){
            $cookieStore.put('userCookie', data);
        }

        $scope.buttonEnabled = false;

        $scope.inputChanges = function(){
            console.log("input changes");
            if($scope.user.email != null
                && $scope.user.email != ""
                && $scope.user.password != null
                && $scope.user.password != ""){
                $scope.buttonEnabled = true;
            }
            else{
                $scope.buttonEnabled = false;
            }
        }

        $scope.login = function(){
            $http({
                method : 'POST',
                url :   '/AuthenticationServlet',
                data :  'method=' + 'LOGIN' + '&'+
                    'user=' + angular.toJson($scope.user),
                headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
            }).success(function(data, status) {
                if("WRONG" == data){
                    $scope.showLogin = true;
                    $scope.showAlert($translate('loginWrong'));
                }
                else{
                    $scope.showLogin = false;
                    $scope.setUserCookies(data);
                    $scope.user = data;
                    //$scope.showAlert($translate('welcome')+ " " + data.name+ "!");
                    window.location.href = "#/tab/alert-list-earth";
                }
            });
        }

        $scope.goToRegistration = function(){
            window.location.href = "#/tab/register";
        }

        $scope.backToLogin = function(){
            window.location.href = "#/tab/login";
        }

        $scope.backToHome = function(){
            window.location.href = "#/tab/alert-new ";
        }
    })

    .controller('HelpCtrl', function($scope, $http,$filter, $ionicPopup, $cookieStore) {
        console.log("initialize help controller");

        $scope.downloadHelpGuide = function(){
            window.location="/Accesenville.pdf"; target="_blank";
        }
    })

