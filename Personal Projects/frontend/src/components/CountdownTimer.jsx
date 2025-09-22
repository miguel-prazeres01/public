import { useEffect, useState, useRef } from 'react';
import { Text } from "@chakra-ui/react";

const CountdownTimer = ({ finishTimeString, setRefreshTriggerEuPago }) => {
	const [remainingSeconds, setRemainingSeconds] = useState(() => {
		const finishTime = new Date(finishTimeString.split('.')[0]);
		const now_aux = new Date();
		const now = now_aux.setHours(finishTime.getHours());
		return Math.max(0, Math.floor((finishTime - now) / 1000));
	});

	useEffect(() => {
		const interval = setInterval(() => {
			setRemainingSeconds((prev) => {
				if (prev <= 1) {
					clearInterval(interval);
					return 0;
				}
				return prev - 1;
			});
		}, 1000);

		return () => clearInterval(interval);
	}, []);

    useEffect(() => {
        if (remainingSeconds == 0){
            setRefreshTriggerEuPago(prev => prev +1);
        }
    }, [remainingSeconds]);


	const formatTime = (secs) => {
		const minutes = Math.floor(secs / 60);
		const seconds = secs % 60;
		return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
	};

	return (<Text align={"center"}>Tempo restante: {formatTime(remainingSeconds)}</Text>);
};


export default CountdownTimer;
