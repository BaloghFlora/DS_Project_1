import { HOST } from '../commons/hosts';
import RestApiClient from "../commons/api/rest-client";

const endpoint = {
    consumption: '/api/monitoring/consumption'
};

function getHourlyConsumption(deviceId, date, callback) {
    // The query params must match the Controller's @RequestParam names
    let request = new Request(HOST.backend_api + endpoint.consumption + `?deviceId=${deviceId}&day=${date}`, {
        method: 'GET',
    });
    console.log("Fetching consumption: " + request.url);
    RestApiClient.performRequest(request, callback);
}

export {
    getHourlyConsumption
};