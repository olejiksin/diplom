import React from 'react';
import {AppWithServerData, MainActions, MainState, MicroAppWithServerData, Worker} from "../types/types";
import {Loader} from "./loader";

const {useReducer, useState} = React;

interface Props {
    addNewMicroApp: (app: MicroAppWithServerData, login: string) => void;
    addNewApp: (app: AppWithServerData, login: string) => void;
    currentUser: string;
    deployStatus: boolean;
}

function reducer(state: MainState, action: MainActions) {
    switch (action.type) {
        case 'appLang': {
            return {
                ...state, appLang: action.payload
            }
        }
        case 'appType': { //mono or micro
            return {
                ...state, appType: action.payload
            }
        }
        case 'serverType': { //own sever or company
            return {
                ...state, serverType: action.payload
            }
        }
        case 'port': {
            return {
                ...state, port: action.payload
            }
        }
        case 'rep': {
            return {
                ...state, rep: action.payload
            }
        }
        case 'appName': {
            return {
                ...state, appName: action.payload
            }
        }
        case 'ip': {
            return {
                ...state, ip: action.payload
            }
        }
        case 'login': {
            return {
                ...state, loginForServer: action.payload
            }
        }
        case 'pass': {
            return {
                ...state, passwordForServer: action.payload
            }
        }
        case 'worker': {
            return {
                ...state, workerList: [...state.workerList, action.payload]
            }
        }
        case 'workerIp': {
            return {
                ...state, workerIp: action.payload
            }
        }
        case 'workerPas': {
            return {
                ...state, workerPassword: action.payload
            }
        }
        case 'workerLogin': {
            return {
                ...state, workerLogin: action.payload
            }
        }
        case 'subMicroName': {
            return {
                ...state, project: action.payload
            }
        }
        case 'subMicroPort': {
            return {
                ...state, subMicroPort: action.payload
            }
        }
        default: {
            return {
                ...state
            }
        }
    }
}

export const AppControl: React.FC<Props> = props => {

    const initState: MainState = {
        loginForServer: null,
        passwordForServer: null,
        serverType: null,
        appType: null,
        rep: 'default',
        ip: 'default',
        port: 'default',
        appName: 'default',
        appLang: 'Java(SB)',
        workerList: [],
        workerIp: '',
        workerPassword: '',
        workerLogin: '',
        project: 'default',
        subMicroPort: ''
    };

    const [state, dispatch] = useReducer(reducer, initState);
    const [projectPort, setProjectPort] = useState(new Map());

    const addProjectPort = (e: React.SyntheticEvent<HTMLButtonElement>) => {
        let map: Map<string, string>;
        map = projectPort;
        map.set(state.project, state.subMicroPort);
        setProjectPort(map);
    }

    const changer = (e: React.SyntheticEvent<HTMLInputElement>, type: string) => {
        dispatch({type: type, payload: e.currentTarget.value});
    };
    const changerSel = (e: React.SyntheticEvent<HTMLSelectElement>, type: string) => {
        dispatch({type: type, payload: e.currentTarget.value});
    };
    const addApp = (e: React.SyntheticEvent<HTMLButtonElement>): void => {
        let app: AppWithServerData = {
            appLang: state.appLang,
            id: "1",
            serverType: state.serverType,
            name: state.appName,
            ip: state.ip,
            link: state.rep,
            port: state.port,
            status: 'online',
            appType: state.appType,
            login: state.loginForServer,
            password: state.passwordForServer
        };
        props.addNewApp(app, props.currentUser);
        dispatch({type: 'button', payload: true});
    };
    const addAppMicro = (e: React.SyntheticEvent<HTMLButtonElement>): void => {
        let app: MicroAppWithServerData = {
            name: state.appName,
            id: "1",
            // serverType: state.serverType,
            link: state.rep,
            ip: state.ip,
            status: 'online',
            appType: state.appType,
            login: state.loginForServer,
            password: state.passwordForServer,
            workerList: state.workerList,
            projectPort: projectPort
        }
        props.addNewMicroApp(app, props.currentUser);
    }

    const addWorkerNode = (e: React.SyntheticEvent<HTMLButtonElement>): void => {
        let worker: Worker = {
            ip: state.workerIp,
            login: state.workerLogin,
            password: state.workerPassword
        };
        dispatch({type: 'worker', payload: worker});
        dispatch({type: 'workerIp', payload: ''});
        dispatch({type: 'workerPas', payload: ''});
        dispatch({type: 'workerLogin', payload: ''});
    }

    return (
        <div>
            {props.deployStatus === false ?
                <div>
                    <h2 id={'h2-loader'}>Deploying application. Please wait.</h2>
                    <Loader/>
                </div> :
                <div className={'new-app'}>
                    <h2 className={'main-input-label'}>Select application type</h2>
                    <div id={"type-select"}>
                        <div className={'radio-input-apptype'}>
                            <input type={'radio'} name={'appType'} id={'radio-1'} value={'mono'}
                                   onChange={(e) => changer(e, 'appType')}/>
                            <label className="radio-label" htmlFor={'radio-1'}>Monolith</label>
                        </div>
                        <div className={'radio-input-apptype'}>
                            <input type={'radio'} name={'appType'} id={'radio-2'} value={'micro'}
                                   onChange={(e) => changer(e, 'appType')}/>
                            <label className="radio-label" htmlFor={'radio-2'}>Microservices</label>
                        </div>
                    </div>
                    {state.appType !== null ?
                        <div>
                            <label className="main-input-label">Link of app (Git repository)</label>
                            <input type="text" className="text-input-main" id="link" name={'rep'}
                                   onChange={(e) => changer(e, 'rep')}/>
                        </div> : null}
                    {state.appType === 'mono' ?
                        <div>
                            <label className={'main-input-label'}>Name for app</label>
                            <input className={'text-input-main'} type={'text'} name={'appName'}
                                   onChange={(e) => changer(e, 'appName')}/>
                            <br/>
                            <p className={'main-input-label'}>Which server do u wanna use?</p>
                            <input className={'radio-input'} type={'radio'} name={'serverType'} value={'own'}
                                   onChange={(e) => changer(e, 'serverType')}/>
                            <label className="radio-label">Own server</label>
                            <input className={'radio-input'} type={'radio'} name={'serverType'} value={'company'}
                                   onChange={(e) => changer(e, 'serverType')}/>
                            <label className="radio-label">Company server</label>
                            {state.serverType === "own" && state.appType === 'mono' ?
                                <div className="div-own-server">
                                    <br/>
                                    <label className={'main-input-label'}>Server ip</label>
                                    <input className={'text-input-main'} type={'text'} name={'serverId'}
                                           onChange={(e) => changer(e, 'ip')}/>
                                    <br/>
                                    <label className={'main-input-label'}>Login </label>
                                    <input className={'text-input-main'} type={'text'} name={'login'}
                                           onChange={(e) => changer(e, 'login')}/>
                                    <br/>
                                    <label className={'main-input-label'}>Password</label>
                                    <input className={'text-input-main'} type={'password'} name={'password'}
                                           onChange={(e) => changer(e, 'pass')}/>
                                </div>
                                : null}
                            <br/>
                            <label className={'main-input-label'}>Port </label>
                            <input type="text" className="text-input-main" name='port'
                                   onChange={(e) => changer(e, 'port')}/>
                            <br/>
                            <label className={'main-input-label'}>App language</label>
                            <select className="select-language" name={'appLang'} size={1} id={'appLangSelector'}
                                    onChange={(e) => changerSel(e, 'appLang')}>
                                <option disabled={true}>Select language of app</option>
                                <option value={'Java(SB)'}>Java (Spring Boot app)</option>
                                <option value={'Node.JS'}>Node.JS</option>
                                <option value={'Python'}>Python</option>
                                <option value={'C#'}>C#</option>
                            </select>
                            <br/>
                            <button className={'btn btn-new-app'} onClick={(e) => addApp(e)}>Add</button>
                        </div>
                        : null}
                    {state.appType === "micro" ?
                        <div className="div-own-server">
                            <label className={'main-input-label'}>Name for app</label>
                            <input className={'text-input-main'} type={'text'} name={'appName'}
                                   onChange={(e) => changer(e, 'appName')}/>
                            <br/>
                            <div className="project-port">
                                <div>
                                    <label className={'project-port-label'}>Submicro name</label>
                                    <label className={'project-port-label'}>Port</label>
                                </div>
                                <br/>
                                <input className={'project-port-input'} type="text" name={'subMicroName'}
                                       onChange={(e) => changer(e, 'subMicroName')}/>
                                <input className={'project-port-input'} type="text" name={'subMicroPort'}
                                       onChange={(e) => changer(e, 'subMicroPort')}/>
                                       <br/>
                                <button className={'project-port-button btn '} onClick={(e) => {
                                    addProjectPort(e)}}>Add</button>
                            </div>
                            <br/>
                            <h3 className="main-input-label">Master node</h3>
                            <label className={'main-input-label'}>Server ip</label>
                            <input className={'text-input-main'} type={'text'} name={'serverId'}
                                   onChange={(e) => changer(e, 'ip')}/>
                            <br/>
                            <label className={'main-input-label'}>Login </label>
                            <input className={'text-input-main'} type={'text'} name={'login'}
                                   onChange={(e) => changer(e, 'login')}/>
                            <br/>
                            <label className={'main-input-label'}>Password</label>
                            <input className={'text-input-main'} type={'password'} name={'password'}
                                   onChange={(e) => changer(e, 'pass')}/>
                            <br/>
                            <h3 className="main-input-label">Worker node</h3>
                            <label className={'main-input-label'}>Server ip</label>
                            <input className={'text-input-main'} type={'text'} name={'serverId'} value={state.workerIp}
                                   onChange={(e) => changer(e, 'workerIp')}/>
                            <br/>
                            <label className={'main-input-label'}>Login </label>
                            <input className={'text-input-main'} type={'text'} name={'login'} value={state.workerLogin}
                                   onChange={(e) => changer(e, 'workerLogin')}/>
                            <br/>
                            <label className={'main-input-label'}>Password</label>
                            <input className={'text-input-main'} type={'password'} name={'password'}
                                   value={state.workerPassword}
                                   onChange={(e) => changer(e, 'workerPas')}/>
                            <br/>
                            <button className="btn new-worker-btn" onClick={(e) => addWorkerNode(e)}>Add worker node
                            </button>
                            <button className={'btn btn-new-app'} onClick={(e) => addAppMicro(e)}>Add</button>
                        </div>
                        :
                        null}
                </div>}
        </div>
    );
}
