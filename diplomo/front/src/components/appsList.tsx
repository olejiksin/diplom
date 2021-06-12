import React, {useState} from "react";
import {App, MicroApp} from "../types/types";

interface Props {
    apps: App[];
    editApp: (app: App) => void;
    removeApp: (id: string) => void;
    startOrStopApp: (id: string) => void;
    microservices: MicroApp[];
}

export const AppList: React.FC<Props> = props => {
    const {apps, removeApp, startOrStopApp} = props;
    const [appListType, setAppListType] = useState('monolith');


    const startOrStop = (e: React.SyntheticEvent<HTMLButtonElement>, id: string): void => {
        startOrStopApp(id);
    };
    const deleter = (e: React.SyntheticEvent<HTMLButtonElement>, id: string): void => {
        let decision = window.confirm('Точно удалить?');
        if (decision) {
            removeApp(id);
        }
    };
    const showHideMicroList = (e: React.MouseEvent<HTMLElement>): void => {
        let el = e.currentTarget;
        let parent = el.parentElement;
        if (parent !== null) {
            let child = parent.children as HTMLCollectionOf<HTMLElement>;
            console.log(child);
            if (!el.classList.contains('drop-micro-str-up')) {
                el.classList.add('drop-micro-str-up');
                child[4].style.display = 'flex';
            } else {
                el.classList.remove('drop-micro-str-up');
                child[4].style.display = 'none';
            }
        }
    }

    const list = apps[0] !== undefined ? (apps.map((app: App, key: number) => {
            return (
                <tr key={key}>
                    <td>{app.name}</td>
                    <td>{app.ip}</td>
                    <td>{app.port}</td>
                    <td className="td-td">{app.link}</td>
                    <td>{app.appLang}</td>
                    {/*<td>{app.appType === 'mono' ? 'Monolith' : 'Microservice'}</td>*/}
                    <td>
                        <button className={`btn ${app.status === 'online' ? 'btn-delete_stop' : 'btn-start'}`}
                                onClick={(e) => {
                                    startOrStop(e, app.id)
                                }}>{app.status === 'online' ? 'Stop' : 'Start'}</button>
                        <button className="btn btn-delete_stop" onClick={(e) => deleter(e, app.id)}>Delete app</button>
                    </td>
                </tr>
            )
        }))
        : null;

    const microList = props.microservices[0] !== undefined ? (props.microservices.map((app: MicroApp, key: number) => {
        return (
            <tr key={key}>
                <td>{app.name}</td>
                <td className="td-td">{app.link}</td>
                <td className="drop-micro" onClick={(e) => {
                    showHideMicroList(e)
                }}/>
                {app.micros !== undefined ? app.micros.map((subApp, subKey) => {
                    return (
                        <td className={'appList-micro-sublist'} key={subKey}>
                            <table>
                                <tbody>
                                <tr>
                                    <td>{subApp.name}</td>
                                    <td>{subApp.appLang}</td>
                                    <td>{subApp.ip}</td>
                                    <td>{subApp.port}</td>
                                    <td>
                                        {/*<button*/}
                                        {/*    className={`btn ${app.status === 'online' ? 'btn-delete_stop' : 'btn-start'}`}*/}
                                        {/*    onClick={(e) => {*/}
                                        {/*        startOrStop(e, app.id)*/}
                                        {/*    }}>{app.status === 'online' ? 'Stop' : 'Start'}</button>*/}
                                        {/*<button className="btn btn-delete_stop"*/}
                                        {/*        onClick={(e) => deleter(e, app.id)}>Delete app*/}
                                        {/*</button>*/}
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    )
                }) : null}
            </tr>
        )
    })) : null;

    return (
        <div>
            <div id='app-type-selector'>
                <button
                    className={appListType === 'monolith' ? 'app-type-selector-div header-link-selected' : 'app-type-selector-div'}
                    onClick={() => setAppListType('monolith')}>Monolith apps
                </button>
                <button
                    className={appListType !== 'monolith' ? 'app-type-selector-div header-link-selected' : 'app-type-selector-div'}
                    onClick={() => setAppListType('micro')}>Microservice apps
                </button>
            </div>
            {props.apps.length > 0 ?
                appListType === 'monolith' ?
                    <table className={'appList'}>
                        <tbody>
                        {list}
                        </tbody>
                    </table>
                    :
                    <table className="appList-micro">
                        <tbody>
                        {microList}
                        </tbody>
                    </table>
                : <h2 className={'oops'}>You dont have any apps. You can add new in "Add new app" tab.</h2>
            }
        </div>
    );
};
