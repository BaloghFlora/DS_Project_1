// src/device/device-api.js
import { HOST } from '../commons/hosts';
import RestApiClient from "../commons/api/rest-client";

const endpoint = {
    devices: '/api/devices',
    myDevices: '/api/devices/my-devices', 
    deviceUsers: '/api/device-users',
    users: '/api/users'
};

// --- NEW FUNCTION ---
function getMyDevices(callback) {
    let request = new Request(HOST.backend_api + endpoint.myDevices, {
        method: 'GET',
    });
    RestApiClient.performRequest(request, callback);
}

function getUsers(callback) {
    let request = new Request(HOST.backend_api + endpoint.users, {
        method: 'GET',
    });
    RestApiClient.performRequest(request, callback);
}
function getDevices(callback) {
    let request = new Request(HOST.backend_api + endpoint.devices, {
        method: 'GET',
    });
    RestApiClient.performRequest(request, callback);
}

function postDevice(device, callback){
    let request = new Request(HOST.backend_api + endpoint.devices , {
        method: 'POST',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(device)
    });
    RestApiClient.performRequest(request, callback);
}

function registerDevice(device, callback){
    let request = new Request(HOST.backend_api + endpoint.devices , {
        method: 'POST',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(device)
    });
    RestApiClient.performRequest(request, callback);
}

function postUser(user, callback){
    let request = new Request(HOST.backend_api + endpoint.users , {
        method: 'POST',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    });
    console.log("URL: " + request.url);
    RestApiClient.performRequest(request, callback);
}
// --- NEW FUNCTION ---
function updateDevice(deviceId, device, callback) {
    let request = new Request(HOST.backend_api + endpoint.devices + `/${deviceId}`, {
        method: 'PUT',
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(device)
    });
    RestApiClient.performRequest(request, callback);
}

function deleteDevice(deviceId, callback) {
    let request = new Request(HOST.backend_api + endpoint.devices + `/${deviceId}`, {
        method: 'DELETE',
    });
    RestApiClient.performRequest(request, callback);
}

function assignDevice(deviceId, userId, callback) {
    let request = new Request(HOST.backend_api + endpoint.deviceUsers + `/assign?deviceId=${deviceId}&userId=${userId}`, {
        method: 'POST',
    });
    RestApiClient.performRequest(request, callback);
}

export {
    getMyDevices, // <-- Export new function
    getDevices,
    postDevice,
    getUsers,
    updateDevice, // <-- Export new function
    deleteDevice,
    assignDevice,
    postUser, // <-- Export new function
    registerDevice
};