// src/commons/api/rest-client.js

function performRequest(request, callback) {
    
    // Get user from localStorage
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
        const user = JSON.parse(storedUser);
        if (user && user.token) {
            // Add Authorization header
            request.headers.set('Authorization', `Bearer ${user.token}`);
        }
    }

    fetch(request)
        .then(
            function(response) {
                if (response.status === 401) {
                    // Auto-logout on 401
                    localStorage.removeItem('user');
                    window.location.href = '/login'; 
                    return;
                }
                
                if (response.ok) {
                    // Handle 204 No Content
                    if (response.status === 204) {
                        return Promise.resolve(null);
                    }
                    return response.json();
                }
                else {
                    return response.json().then(err => Promise.reject(err));
                }
            })
        .then(json => {
            callback(json, request.status, null);
        })
        .catch(function (err) {
            //catch any other unexpected error
            callback(null, 1, err)
        });
}

module.exports = {
    performRequest
};