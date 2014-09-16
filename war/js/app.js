// Ionic Starter App, v0.9.20

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('starter', ['ionic', 'starter.controllers', 'starter.services','pascalprecht.translate'])

    .run(function($ionicPlatform) {
        $ionicPlatform.ready(function() {
            //StatusBar.styleDefault();
        });
    })

    .config(function($stateProvider, $urlRouterProvider) {

        // Ionic uses AngularUI Router which uses the concept of states
        // Learn more here: https://github.com/angular-ui/ui-router
        // Set up the various states which the app can be in.
        // Each state's controller can be found in controllers.js
        $stateProvider

            // setup an abstract state for the tabs directive
            .state('tab', {
                url: "/tab",
                abstract: true,
                templateUrl: "templates/tabs.html"
            })

            // Each tab has its own nav history stack:

            .state('tab.alert-new', {
                url: '/alert-new',
                views: {
                    'tab-alert-new': {
                        templateUrl: 'templates/tab-alert-new.html',
                        controller: 'AlertNewCtrl'
                    }
                }
            })

            .state('tab.alert-create', {
                url: '/alert-create',
                views: {
                    'tab-alert-new': {
                        templateUrl: 'templates/tab-alert-create.html',
                        controller: 'AlertNewCtrl'
                    }
                }
            })

            .state('tab.alert-list', {
                url: '/alert-list',
                views: {
                    'tab-alert-list': {
                        templateUrl: 'templates/tab-alert-list.html',
                        controller: 'AlertListCtrl'
                    }
                }
            })

            .state('tab.alert-list-earth', {
                url: '/alert-list-earth',
                views: {
                    'tab-alert-list': {
                        templateUrl: 'templates/tab-alert-list-earth.html',
                        controller: 'AlertListCtrl'
                    }
                }
            })

            .state('tab.alert-detail', {
                url: '/alert-detail/:alertId',
                views: {
                    'tab-alert-list': {
                        templateUrl: 'templates/tab-alert-detail.html',
                        controller: 'AlertDetailCtrl'
                    }
                }
            })

            .state('tab.alert-detail-full-message', {
                url: '/alert-detail-full-message',
                views: {
                    'tab-alert-list': {
                        templateUrl: 'templates/tab-alert-detail-full-message.html',
                        controller: 'AlertDetailCtrl'
                    }
                }
            })

            .state('tab.alert-edit', {
                url: '/alert-edit/:alertId',
                views: {
                    'tab-alert-list': {
                        templateUrl: 'templates/tab-alert-edit.html',
                        controller: 'AlertDetailCtrl'
                    }
                }
            })

            .state('tab.register', {
                url: '/register',
                views: {
                    'tab-register': {
                        templateUrl: 'templates/tab-register.html',
                        controller: 'AuthCtrl'
                    }
                }
            })

            .state('tab.login', {
                url: '/login',
                views: {
                    'tab-register': {
                        templateUrl: 'templates/tab-login.html',
                        controller: 'AuthCtrl'
                    }
                }
            })

            .state('tab.help', {
                url: '/help',
                views: {
                    'tab-register': {
                        templateUrl: 'templates/tab-help.html',
                        controller: 'HelpCtrl'
                    }
                }
            })

            .state('tab.register-ok', {
                url: '/register-ok',
                views: {
                    'tab-register': {
                        templateUrl: 'templates/tab-register-ok.html',
                        controller: 'AuthCtrl'
                    }
                }
            })

            .state('tab.register-ko', {
                url: '/register-ko',
                views: {
                    'tab-register': {
                        templateUrl: 'templates/tab-register-ko.html',
                        controller: 'AuthCtrl'
                    }
                }
            })



        // if none of the above states are matched, use this as the fallback
        $urlRouterProvider.otherwise('/tab/alert-list-earth');

    })

    .config(function($translateProvider) {
        $translateProvider.useStaticFilesLoader({
            prefix: '/lang/',
            suffix: '.json'
        });

        var langs = ['en','it','fr'];
        var fallback = langs[0];

        $translateProvider.determinePreferredLanguage(function() {
            console.log("NAVIGATOR");
            var tag = navigator.language || navigator.browserLanguage || navigator.systemLanguage || navigator.userLanguage;
            if(tag.indexOf("en") > -1){
            	tag = "en"; // should be 'en us' for example...
            }
            for(var i = 0; i<langs.length; i++){
                if(tag.indexOf(langs[i]) > -1){
                    return langs[i];
                }
            }
            return fallback;
        });
    });

