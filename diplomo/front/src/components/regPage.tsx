import React, {SyntheticEvent} from 'react';
import {Link, RouteComponentProps} from "react-router-dom";
import axios from 'axios';

import {LogRegAction} from '../types/types';

const {useReducer} = React;


function reducer(state: RegState, action: LogRegAction) {
    switch (action.type) {
        case 'login' : {
            return {
                ...state, login: action.payload
            }
        }
        case 'pas': {
            return {
                ...state, password: action.payload
            }
        }
        default : {
            return {...state};
        }
    }
}

interface UserReg {
    login:string;
    password:string;
}
interface RegState{
    login:string;
    password:string;
};
export const RegPage: React.FC <RouteComponentProps>= props => {

    const initState: RegState = {
        login:'',
        password: ''
    };

    const [state, dispatch] = useReducer(reducer, initState);

    const onChange = (e: React.SyntheticEvent<HTMLInputElement>, typee: string):void => {
        dispatch({type: typee, payload: e.currentTarget.value});
    };

    const reg = (event:SyntheticEvent<HTMLButtonElement>):void => {
        let data: UserReg = {
            login:state.login,
            password: state.password
        };
        axios.post('/reg', data)
            .then(() => {
                props.history.push('/login')
            })
            .catch((er) => {
                alert('Please, enter another login');
            })
    };

    return (
        <div>
            <div className={'header'} style={{background: 'white'}}>
                    <Link to={'/help'} className={'btn'}>Help</Link>
                    <Link to={'/login'} className={'btn'}>Sign in</Link>
                    <Link to={'/reg'} className={'btn btn-selected'}>Sign Up</Link>
            </div>
            <div className={'reg'}>
                <label className={'log-label'}>Login</label>
                <br/>
                <input className={'text-input'} type={'text'} name={"login"} id={"login"}
                       onChange={(event) => onChange(event, 'login')}/>
                <label className={'log-label'}>Password</label>
                <br/>
                <input className={'text-input'} type={'password'} name={"password"} id={"password"}
                       onChange={(event) => onChange(event, 'pas')}/>
                <button className={"log-reg-btn"} onClick={(event) => reg(event)}>Sign up</button>
            </div>
        </div>);
};


