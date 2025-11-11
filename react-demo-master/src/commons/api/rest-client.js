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
                // [!code ++]
                // Handle 204 No Content (e.g., Delete)
                if (response.status === 204) {
                    callback(null, response.status, null);
                    return; // Stop processing
                }

                // Handle other OK responses
                if (response.ok) {
                    const contentType = response.headers.get("content-type");
                    if (contentType && contentType.includes("application/json")) {
                        // It's JSON, parse it
                        response.json().then(json => callback(json, response.status, null));
                    } else {
                        // It's text (like "User assigned..."), return text
                        response.text().then(text => callback(text, response.status, null));
                    }
                // [!code --]
                } else if (response.status === 401) {
                    // Unauthorized - clear token and redirect to login
                    localStorage.removeItem('user');
                    window.location.href = '/login';
                    callback(null, response.status, 'Unauthorized');
                } else {
                    // [!code --]
                    // Original error logic
                    response.json().then(err => callback(null, response.status, err));
                    // [!code ++]
                    // Try to parse error as JSON, fallback to text
                    response.json()
                        .then(err => callback(null, response.status, err))
                        .catch(() => response.text().then(errText => callback(null, response.status, errText)));
                    // [!code --]
                }
            })
        .catch(function (err) {
            // Network error
            callback(null, 0, err);
        });
};

const RestApiClient = {
    performRequest
};

export default RestApiClient;
