import React from 'react';
import {singup} from '../api/apiCalls';
import Input from "../components/Input";
import {withTranslation} from 'react-i18next';

class RegisterPage extends React.Component {

    state = {
        username: null,
        password: null,
        password2: null,
        pendingApiCall: false,
        messages: {}
    };

    onChange = event => {
        const {name, value} = event.target;
        const messages = {...this.state.messages};
        messages[name] = undefined;

        if (name === 'password' || name === 'password2') {
            if (name === 'password' && value !== this.state.password2) {
                messages.password2 = 'Password mismatch';
            } else if (name === 'password2' && value !== this.state.password) {
                messages.password2 = 'Password mismatch';
            } else {
                messages.password2 = undefined;
            }
        }

        this.setState({
            [name]: value,
            messages
        })
    };

    onClickSignUp = async event => {
        event.preventDefault();

        const {username, password, password2} = this.state;

        const body = {
            username,
            password,
            password2
        };

        this.setState({pendingApiCall: true});

        try {
            const response = await singup(body);
        } catch (error) {
            if (error.response.data.messages) {
                this.setState({
                    messages: error.response.data.messages
                });
            }
        }
        this.setState({pendingApiCall: false});


    }

    render() {
        const {pendingApiCall, messages} = this.state;
        const {username, password, password2} = messages;
        const {t} = this.props;

        return (
            <div className="container">
                <div className="row">
                    <div className="col-lg-10 col-xl-9 mx-auto">
                        <div className="card card-signin flex-row my-5">
                            <div className="card-img-left d-none d-md-flex">
                            </div>
                            <div className="card-body">
                                <h5 className="card-title text-center">{t('Sign Up')}</h5>
                                <form className="form-signin">
                                    <hr/>
                                    <Input type="text" label="Username" name="username" error={username}
                                           onChange={this.onChange}/>
                                    <Input type="password" label="Password"
                                           name="password" error={password}
                                           onChange={this.onChange}/>
                                    <Input type="password"
                                           label="Confirm password"  name="password2"
                                           error={password2} onChange={this.onChange}/>
                                    <hr/>
                                    <div className="text-center">
                                        <button className="btn btn-lg btn-primary btn-block text-uppercase"
                                                onClick={this.onClickSignUp}
                                                disabled={pendingApiCall || password2 !== undefined}>
                                            {pendingApiCall &&
                                            <span className="spinner-border spinner-border-sm"/>} Sign Up
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

const SignUpPageWithTranslation = withTranslation()(RegisterPage)
export default SignUpPageWithTranslation;

