import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { jwtDecode } from 'jwt-decode';

type AdminContextType = {
    isAdmin: boolean;
    login: (token: string) => void;
    logout: () => void;
};


interface JwtPayload {
    username: string;
    role: string;
    exp: number;
}

const AdminContext = createContext<AdminContextType>({
    isAdmin: false,
    login: () => { },
    logout: () => { },
});

export const AdminProvider = ({ children }: { children: React.ReactNode }) => {
    const [isAdmin, setIsAdmin] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem('adminToken');
        if (token) {
            try {
                const decoded = jwtDecode<JwtPayload>(token);
                setIsAdmin(decoded?.role === 'ADMIN');
            } catch {
                setIsAdmin(false);
            }
        }
    }, []);

    const login = (token: string) => {
        localStorage.setItem('adminToken', token);
        try {
            const decoded = jwtDecode<JwtPayload>(token);
            setIsAdmin(decoded?.role === 'ADMIN');
        } catch {
            setIsAdmin(false);
        }
    };

    const logout = () => {
        localStorage.removeItem('adminToken');
        setIsAdmin(false);
    };

    const memoizedValue = useMemo(() => ({ isAdmin, login, logout }), [isAdmin]);

    return (
        <AdminContext.Provider value={memoizedValue}>
            {children}
        </AdminContext.Provider>
    );
};


export const useAdmin = () => useContext(AdminContext);
