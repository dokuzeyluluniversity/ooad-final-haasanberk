import axios from "axios";


export const singup =  (body) => {
    return axios.post('/users/register', body);
}

export const singin =  (body) => {
    return axios.post('/users/get-token', {body});
}

export const changeLanguage = language => {
    axios.defaults.headers['accept-language'] = language;
};

export const getUsers = (page = 0, size = 3) => {
    return axios.get('/api/users?page=${page}&size=${size}');
};

export const setAuthorizationHeader = ({ username, password, isLoggedIn }) => {
    if (isLoggedIn) {
        const authorizationHeaderValue = `Bearer ${btoa(username + ':' + password)}`;
        axios.defaults.headers['Authorization'] = authorizationHeaderValue;
    } else {
        delete axios.defaults.headers['Authorization'];
    }
};

export const getUser = username => {
    return axios.get(`/api/users/${username}`);
};

export const postBook = books => {
    return axios.post('/api/books', books);
};

export const getBook = (book, page = 0) => {
    const path = book ? '/api/books?id=${id}' : '/api/books?page=';
    return axios.get(path + page);
};

export const getEntires= (username, page = 0) => {
    const path = username ? '/api/entries?username=${username}' : '/api/entries?username=';
    return axios.get(path + page);
};

export const getOldBooks = (id, username) => {
    const path = username ? '/api/books/${id}' : '/api/books/${id}';
    return axios.get(path);
};

export const getNewBookCount = (id, page = 0) => {
    const path = id ? '/api/books/entires/${id}?count=true' : '/api/books?page=';
    return axios.get(path + page);
};

export const getNewBooks = (book, title) => {
    const path = book ? '/api/books?id=${id}&direction=after' : '/api/books?{id}&direction=after';
    return axios.get(path);
};
