import React, { createContext, useState, useContext } from 'react';
import { login as apiLogin } from './auth-api';
import { useNavigate } from 'react-router-dom';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(() => {
        const storedUser = localStorage.getItem('user');
        return storedUser ? JSON.parse(storedUser) : null;
    });
    
    const navigate = useNavigate();

    const login = async (username, password) => {
        try {
            const data = await apiLogin(username, password);
            const userData = {
                username: data.user,
                token: data.token,
                roles: data.roles.split(',')
            };
            localStorage.setItem('user', JSON.stringify(userData));
            setUser(userData);
            
            // Redirect based on role
            if (userData.roles.includes('ROLE_ADMIN')) {
                navigate('/users');
            } else {
                navigate('/devices');
            }
        } catch (error) {
            console.error("Login failed:", error);
            // You can add error handling here (e.g., set an error state)
            throw new Error("Invalid username or password");
        }
    };

    const logout = () => {
        localStorage.removeItem('user');
        setUser(null);
        navigate('/login');
    };

    const hasRole = (role) => {
        if (!user || !user.roles) return false;
        return user.roles.includes(role);
    };

    const authInfo = {
        user,
        token: user?.token,
        isAdmin: hasRole('ROLE_ADMIN'),
        isUser: hasRole('ROLE_USER'),
        login,
        logout,
    };

    return (
        <AuthContext.Provider value={authInfo}>
            {children}
        </AuthContext.Provider>
    );
};

// Custom hook to use the auth context
export const useAuth = () => {
    return useContext(AuthContext);
};

export default AuthContext;