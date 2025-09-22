import { Button
} from "@chakra-ui/react";


const SeatButton = ({ seat, setReserved, reserved, reservedServer}) => {
	
	const isButtonFree = () =>{
		if (seat.seatState == 'OCCUPIED' || reservedServer.includes(seat.id) || seat.seatState == 'RESERVING'){
			return true;
		} else {
			return false;
		}
	};

	const setColorScheme = () => {
		if (seat.seatState == 'OCCUPIED' || seat.seatState == 'RESERVING' || reservedServer.includes(seat.id)){
			return 'red.600';
		} else if (reserved.includes(seat.id)) {
			return 'blue.100';
		} else if (seat.seatState == 'EMPTY'){
			return 'purple.100';
		}
	};
	

	const handleReserveSeat = () => {
		if (reserved.includes(seat.id)){
			setReserved((prevSeats) => prevSeats.filter((s) => (s !== seat.id)));
		} else {
			setReserved((prevSeats) => [...prevSeats, seat.id]);
		}
		
	};


	return (
		<>
		<Button bg={setColorScheme()} isDisabled={isButtonFree()} onClick={handleReserveSeat}>
			{seat.row}{seat.seatNumber}
		</Button>
		</>
	);
};
export default SeatButton;
