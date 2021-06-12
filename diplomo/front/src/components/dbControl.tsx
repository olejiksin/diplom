import React from 'react';
import {DataBaseWithServer, dbType, MainActions, serverTypeForDeploy} from "../types/types";

const {useReducer} = React;

interface DbControlState {
    dbType: null | dbType,
    serverType: null | serverTypeForDeploy,
    ip: string,
    port: string,
    pas: string,
    user: string,
    file: any,
    name:string
}

function reducer(state: DbControlState, action: MainActions) {
    switch (action.type) {
        case 'ip': {
            return {
                ...state, ip: action.payload
            }
        }
        case 'pas': {
            return {
                ...state, pas: action.payload
            }
        }
        case 'port': {
            return {
                ...state, port: action.payload
            }
        }
        case 'name': {
            return {
                ...state, name: action.payload
            }
        }
        case 'serverType': { //own sever or company
            return {
                ...state, serverType: action.payload
            }
        }
        case 'dbType': {
            return {
                ...state, dbType: action.payload
            }
        }
        case 'user': {
            return {
                ...state, user: action.payload
            }
        }
        case 'file-uploader': {
            return {
                ...state, file: action.payload
            }
        }
        default: {
            return {
                ...state
            }
        }
    }
}

interface Props {
    currentUser: string,
    addNewDB: (file:any,db: DataBaseWithServer, login: string) => void;
}

export const DBControl: React.FC<Props> = props => {

    const initState: DbControlState = {
        ip: "default",
        pas: "default",
        port: "default",
        dbType: 'postgresql',
        serverType: null,
        user: 'default',
        file: null,
        name:'default'
    }
    const [state, dispatch] = useReducer(reducer, initState);

    const changer = (e: React.SyntheticEvent<HTMLInputElement | HTMLSelectElement>, type: string): void => {

        if (type !== 'file-uploader') {
            dispatch({type: type, payload: e.currentTarget.value});
        }else{// @ts-ignore
            dispatch({type:type,payload:e.currentTarget.files[0]})}
    }

    const addDb = (e: React.SyntheticEvent<HTMLButtonElement>): void => {
        alert('Вам нужно подождать пару минут, пока мы все запускаем ');

        let db: DataBaseWithServer = {
            id:"1",
            dbType: state.dbType,
            ip: state.ip,
            pas: state.pas,
            port: state.port,
            serverType: state.serverType,
            status: 'online',
            user: state.user,
            name:state.name
        }
        props.addNewDB(state.file,db, props.currentUser);
    }

    return (
        <div className="new-db">
                <label className="main-input-label" id={'db-main-input-label-first'}>Select database</label>
                <select className="select-language" name={'dbType'} size={1} id={'dbSelector'}
                        onChange={(e) => changer(e, 'dbType')}>
                    <option disabled={true}>Select database</option>
                    <option value={'postgresql'}>PostgreSQL</option>
                    <option value={'mongodb'}>MongoDB</option>
                    <option value={'mysql'}>MySQL</option>
                    <option value={'redis'}>Redis</option>
                </select>
                <br/>
                <label className={'main-input-label'}>Which server do u wanna use?</label>
                <input className={'radio-input'} type={'radio'} name={'serverType'} value={'own'}
                       onChange={(e) => changer(e, 'serverType')}/>
                <label className="radio-label">Own server</label>
                <input className={'radio-input'} type={'radio'} name={'serverType'} value={'company'}
                       onChange={(e) => changer(e, 'serverType')}/>
                <label className="radio-label">Company server</label>
                {state.serverType === "own" ?
                    <div className="div-own-server">
                        <br/>
                        <label className={'main-input-label'}>Server ip</label>
                        <input className={'text-input-main'} type={'text'} name={'serverId'}
                               onChange={(e) => changer(e, 'ip')}/>
                        <br/>
                        <label className={'main-input-label'}>Login </label>
                        <input className={'text-input-main'} type={'text'} name={'login'}
                               onChange={(e) => changer(e, 'user')}/>
                        <br/>
                        <label className={'main-input-label'}>Password</label>
                        <input className={'text-input-main'} type={'password'} name={'password'}
                               onChange={(e) => changer(e, 'pas')}/>
                               <br/>
                    </div>
                    :
                    null
                }
                <label className={'main-input-label'}>Name for db</label>
                <input className={'text-input-main'} type={'text'} name={'appName'}
                       onChange={(e) => changer(e, 'appName')}/>
                <br/>
                <label className={'main-input-label'}>Port </label>
                <input type="text" className="text-input-main" name='port' onChange={(e) => changer(e, 'port')}/>
                <br/>
                <br/>
                <label htmlFor='file-uploader' id='file-uploader-label'>
                    <i>Upload script file</i>
                </label>
                <input onChange={(e) => changer(e, 'file-uploader')} type="file" id='file-uploader' name='file'/>
                <button className="btn btn-new-app" onClick={(e) => addDb(e)}>Add</button>
        </div>
    )
}
