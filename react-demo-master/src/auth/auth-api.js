import { HOST } from '../commons/hosts';
import RestApiClient from '../commons/api/rest-client';

const endpoint = {
    login: '/auth/login'
};

function login(username, password) {
    const formData = new URLSearchParams();
    formData.append('username', username);
    formData.append('password', password);

    let request = new Request(HOST.backend_api + endpoint.login, {
        method: 'POST',
        headers : {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData.toString()
    });

    return new Promise((resolve, reject) => {
        RestApiClient.performRequest(request, (data, status, err) => {
            if (data && status === 200) {
                resolve(data);
            } else {
                reject(err || new Error('Login failed'));
            }
        });
    });
}

export {
    login
};