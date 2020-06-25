import React from 'react';
import UserList from '../components/UserList';
import BookSubmit from '../components/BookSubmit';
import { useSelector } from 'react-redux';
import BookFeed from '../components/BookFeed';

const HomePage = () => {
    const { isLoggedIn } = useSelector(store => ({ isLoggedIn: store.isLoggedIn }));
    return (
        <div className="container">
            <div className="row">
                <div className="col">
                    {isLoggedIn && (
                        <div className="mb-1">
                            <BookSubmit />
                        </div>
                    )}
                    <BookFeed />
                </div>
                <div className="col">
                    <UserList />
                </div>
            </div>
        </div>
    );
};

export default HomePage;