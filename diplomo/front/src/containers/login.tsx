import React from 'react';
import {Link} from "react-router-dom";
import {connect} from "react-redux";

import {login} from "../actions/actions";
import {User, LoginState, LogRegAction} from "../types/types";



const {useReducer} = React;

interface Props {
    login: (user: User, props: any) => void;
}

function reducer(state: LoginState, action: LogRegAction) {
    switch (action.type) {
        case 'email' : {
            return {
                ...state, emailOrLogin: action.payload
            }
        }
        case 'pas': {
            return {
                ...state, password: action.payload
            }
        }
        default: {
            return {...state};
        }
    }
}

const Login: React.FC<Props> = props => {

    const initState: LoginState = {
        emailOrLogin: '',
        password: ''
    };

    const [state, dispatch] = useReducer(reducer, initState);

    const onChange = (e: React.SyntheticEvent<HTMLInputElement>, typee: string) => {
        dispatch({type: typee, payload: e.currentTarget.value});
    };

    const {login} = props;

    const logIn = () => {
        let user: User = {
            login: state.emailOrLogin,
            password: state.password
        };
        login(user, props) ;
    };

    return (
        <div>
            <div className={'header'} style={{background: 'white'}}>
                    <Link to={'/help'} className={'btn'}>Help</Link>
                    <Link to={'/login'} className={'btn btn-selected'}>Sign in</Link>
                    <Link to={'/reg'} className={'btn'}>Sign Up</Link>
            </div>
            <div className={'login'}>
                <label className={'log-label'}>Login</label>
                <input type={"text"} name={"email"} id={"email"} className={'text-input'}
                       onChange={(event) => onChange(event, 'email')}/>
                <br/>
                <label className={'log-label'}>Password</label>
                <input className={'text-input'} type={"password"}
                       onChange={(event) => onChange(event, 'pas')}
                       name={'password'}/>
                <button className={"log-reg-btn"}
                        onClick={() => logIn()}>
                    Log In
                </button>
            </div>
        </div>);
};

const actions = {
    login
};

export default connect(null, actions)(Login);
