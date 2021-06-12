import React from "react";
import {RouteComponentProps} from "react-router";
import {App, Server} from "../types/types";
import serve from "@webpack-cli/serve";

interface Props {
    email: string | null;
    servers: Server[];
    editServer: (server: Server, email: string | null) => void;
    removeServer: (name: string, email: string | null) => void;
}

interface State {
    user: null | string,
    password: null | string,
    name: string;
    ip: string;
    nameServer: string;
}

interface Action {
    type: string;
    payload: any;
}

const {useReducer} = React;

function reducer(state: State, action: Action) {
    switch (action.type) {
        case 'user': {
            return {
                ...state, user: action.payload
            }
        }
        case 'password': {
            return {
                ...state, password: action.payload
            }
        }
        case 'name': {
            return {
                ...state, name: action.payload
            }
        }
        case 'nameServer': {
            return {
                ...state, nameServer: action.payload
            }
        }
        case 'ip': {
            return {
                ...state, ip: action.payload
            }
        }
        default: {
            return {
                ...state, user: null, password: null, ip: 'default', id: 0, name: 'default'
            }
        }
    }
}

export const ServerList: React.FC<Props> = props => {

    const initState: State = {
        user: null,
        password: null,
        name: 'default',
        ip: 'default',
        nameServer: 'default'
    };
    const [state, dispatch] = useReducer(reducer, initState);
    const {servers, editServer, removeServer, email} = props;

    const changer = (e: React.SyntheticEvent<HTMLInputElement>, type: string) => {
        dispatch({type: type, payload: e.currentTarget.value});
    };

    const deleter = (name: string) => {
        removeServer(name, email);
    };

    const edit = () => {
        let server = (props.servers.filter((server) => {
            return server.name === state.name;
        }))[0];
        server = {
            name: state.name !== 'default' ? state.name : server.name,
            ip: state.ip !== 'default' ? state.ip : server.ip,
            status: server.status,
        };
        editServer(server, email);
        let elem = document.getElementById('server-editor');
        if (elem !== null) {
            elem.style.display = 'none';
        }
    };

    const funct = (name: string) => {
        const elem = document.getElementById('server-editor');
        if (elem !== null) {
            elem.style.display = 'block';
            dispatch({type: 'nameServer', payload: name});
        }
    };
    const list = servers[0] !== undefined ? (servers.map((server: Server, key: number) => {
            <tr id={'server' + key}>
                <td>{server.name}</td>
                <td>{server.ip}</td>
                <td>{server.status}</td>
                <td>
                    <button onClick={() => funct(server.name)}>Edit server
                    </button>
                </td>
                <td>
                    <button onClick={() => deleter(server.name)}>Delete server</button>
                </td>
            </tr>
        }
        ))
        : <tr/>;

    return (
        <div className={'server-list'}>
            <table>
                <thead>
                <tr>
                    <td>Server name</td>
                    <td>Ip address</td>
                    <td>Server status</td>
                </tr>
                </thead>
                <tbody>
                {list}
                </tbody>
            </table>
            <div className={'editor-connector'} id={'server-editor'} style={{display: 'none'}}>
                <span className={'closer'}/>
                <label className={'log-label'}>Name</label>
                <input onChange={(e) => changer(e, 'name')} type={'text'} className={'text-input'}/>
                <label className={'log-label'}>Ip</label>
                <input onChange={(e) => changer(e, 'ip')} type={'text'} className={'text-input'}/>
                <button onClick={() => edit()}>Edit</button>
            </div>
        </div>
    );
};
