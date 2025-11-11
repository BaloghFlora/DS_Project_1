import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

const ProtectedRoute = ({ children, adminOnly = false }) => {
    const { user, isAdmin } = useAuth();

    if (!user) {
        // Not logged in, redirect to login page
        return <Navigate to="/login" replace />;
    }

    if (adminOnly && !isAdmin) {
        // Logged in, but not an admin
        // Redirect to a non-admin page (e.g., their devices)
        return <Navigate to="/devices" replace />;
    }

    return children;
};

export default ProtectedRoute;