// src/user/user-api.js
import { HOST } from '../commons/hosts';
import RestApiClient from "../commons/api/rest-client";

const endpoint = {
    // This now correctly points to /api/people
    people: '/api/people' 
};

function getPersons(callback) {
    let request = new Request(HOST.backend_api + endpoint.people, {
        method: 'GET',
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function getPersonById(params, callback){
    let request = new Request(HOST.backend_api + endpoint.people + params.id, {
       method: 'GET'
    });
    console.log(request.url);
    RestApiClient.performRequest(request, callback);
}

function postPerson(user, callback){
    let request = new Request(HOST.backend_api + endpoint.people , {
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
function updateUser(userId, user, callback) {
    let request = new Request(HOST.backend_api + endpoint.people + `/${userId}`, {
        method: 'PUT',
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
function deleteUser(userId, callback) {
    let request = new Request(HOST.backend_api + endpoint.people + `/${userId}`, {
        method: 'DELETE',
    });
    console.log("URL: " + request.url);
    RestApiClient.performRequest(request, callback);
}

export {
    getPersons,
    getPersonById,
    postPerson,
    updateUser, // <-- Export new function
    deleteUser  // <-- Export new function
};