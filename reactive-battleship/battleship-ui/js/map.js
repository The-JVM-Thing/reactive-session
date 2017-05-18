var map = L.map('map', {center: [50.406, 0.255], zoom: 9})

//add a background tile layer
L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibmVpbGR1bmxvcCIsImEiOiJjaXhycGc1bDQwMDQ3MnhxaXFibDl4cXRsIn0.crgrBXCV-JDmTRk0j7Lg8w', {
    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
    maxZoom: 18,
    id: 'mapbox.streets',
    accessToken: 'pk.eyJ1IjoibmVpbGR1bmxvcCIsImEiOiJjaXhycGc1bDQwMDQ3MnhxaXFibDl4cXRsIn0.crgrBXCV-JDmTRk0j7Lg8w'
}).addTo(map);

var missIcon = L.icon({
    iconUrl: '/splash.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -28]
});

var hitIcon = L.icon({
    iconUrl: '/explosion_once.gif',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -28]
});

//Websocket management
var ws;
(function(ws) {
    "use strict";
    if (window.WebSocket) {
        console.log("WebSocket object is supported in your browser");
        ws = new WebSocket("ws://192.168.99.100:8082");

        ws.onopen = function() {
            ws.send('fire the cannon!');
        };

        ws.onmessage = function(e) {
            console.log("echo from server : " + e.data);
            var positionJson = JSON.parse(e.data);
            L.marker([positionJson.latitude, positionJson.longitude], {icon: positionJson.hit ? hitIcon : missIcon}).addTo(map);
            console.log("updated");
            setTimeout(function(){ 
                ws.send('fire the cannon!');;
            }, 2000);
        };

        ws.onclose = function() {
            console.log("onclose");
        };
        ws.onerror = function() {
            console.log("onerror");
        };

    } else {
        console.log("WebSocket object is not supported in your browser");
    }
})(ws);

function sleep(delay) {
        var start = new Date().getTime();
        while (new Date().getTime() < start + delay);
}