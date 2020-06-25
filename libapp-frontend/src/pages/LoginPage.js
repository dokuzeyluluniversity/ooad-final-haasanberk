import React from 'react';
import Input from "../components/Input";
import {singin} from '../api/apiCalls';
import {useTranslation, withTranslation} from 'react-i18next';

class LoginPage extends React.Component {

    state = {
        username: null,
        password: null,

    };

    onChange = event => {
        const {name, value} = event.target;

        this.setState({
            [name]: value,
        })
    };

    onClickLogin = event => {
        event.preventDefault();

        const {username, password} = this.state;

        const body = {
            username,
            password
        };

        singin(body).then(r => console.log(r.data));

    }

    render() {
        return (
            <div className="container">
                <div className="row">
                    <div className="col-lg-6 col-xl-6 mx-auto"><br/>
                        <div className="card card-signin flex-row my-5">
                            <div className="card-body">
                                <h5 className="card-title text-center">Sign Up</h5>
                                <form className="form-signin">
                                    <hr/>
                                    <Input type="text" label="Username" name="username"
                                           onChange={this.onChange}/>

                                    <Input type="password" label="Password" name="password"
                                           onChange={this.onChange}/>

                                    <hr/>
                                    <div className="text-center">
                                        <button className="btn btn-lg btn-primary btn-block text-uppercase"
                                                onClick={this.onClickLogin} >
                                            Sign In
                                        </button>
                                        <small className="text-secondary">-or-</small>
                                        <a className="d-block text-center mt-2 small" href="/">Sign Up</a>
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


const SignUpPageWithTranslation =  withTranslation()(LoginPage)
export default SignUpPageWithTranslation;
