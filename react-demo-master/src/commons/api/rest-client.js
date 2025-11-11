function performRequest(request, callback) {
    // Get the token from localStorage
    const user = localStorage.getItem('user');
    let token = null;
    
    if (user) {
        try {
            const userData = JSON.parse(user);
            token = userData.token;
        } catch (e) {
            console.error('Error parsing user data:', e);
        }
    }

    // Add Authorization header if token exists
    if (token && !request.headers.has('Authorization')) {
        request.headers.set('Authorization', `Bearer ${token}`);
    }

    fetch(request)
        .then(
            function (response) {
                if (response.ok) {
                    response.json().then(json => callback(json, response.status, null));
                } else if (response.status === 401) {
                    // Unauthorized - clear token and redirect to login
                    localStorage.removeItem('user');
                    window.location.href = '/login';
                    callback(null, response.status, 'Unauthorized');
                } else {
                    response.json().then(err => callback(null, response.status, err));
                }
            })
        .catch(function (err) {
            callback(null, 0, err);
        });
}

const RestApiClient = {
    performRequest
};

export default RestApiClient;