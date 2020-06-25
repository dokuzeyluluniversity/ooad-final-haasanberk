import React from 'react';
import RegisterPage from '../pages/RegisterPage';
import LoginPage from '../pages/LoginPage';
import LanguageSellector from '../components/LanguageSellector';
import HomePage from '../pages/HomePage';
import UserPage from '../pages/UserPage';
import { HashRouter as Router, Route, Redirect, Switch } from 'react-router-dom';
import TopBar from '../components/TopBar';
import { useSelector } from 'react-redux';

const App = () => {
    const { isLoggedIn } = useSelector(store => ({
        isLoggedIn: store.isLoggedIn
    }));

    return (
        <div>
            <Router>
                <TopBar />
                <Switch>
                    <Route exact path="/" component={HomePage} />
                    {!isLoggedIn && <Route path="/login" component={LoginPage} />}
                    <Route path="/signup" component={RegisterPage} />
                    <Route path="/user/:username" component={UserPage} />
                    <Redirect to="/" />
                </Switch>
            </Router>
            <LanguageSellector />
        </div>
    );
};

export default App;