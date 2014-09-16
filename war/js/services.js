angular.module('starter.services', [])

    .factory('Helmets', function ($http, $q) {
        var urlBase = '/HelmetServlet';

        return {
            getAll: function() {
                return $http({
                        method : 'POST',
                        url :   urlBase,
                        data :  'method=' + 'GET_ALL_HELMET',
                        headers : {'Content-Type' : 'application/x-www-form-urlencoded'}
                    })
                    .then(function(response) {
                        if (typeof response.data === 'object') {
                            return response.data;
                        } else {
                            // invalid response
                            return $q.reject(response.data);
                        }

                    }, function(response) {
                        // something went wrong
                        return $q.reject(response.data);
                    });
            }
        };
    })