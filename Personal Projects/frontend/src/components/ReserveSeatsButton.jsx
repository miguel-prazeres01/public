import { Button, Container, Flex, Text, Modal,
	ModalOverlay,
	ModalContent,
	ModalHeader,
	ModalFooter,
	ModalBody,
	ModalCloseButton, useDisclosure,
	FormControl,
	FormLabel,
	Input,
	Popover,
  PopoverTrigger,
  PopoverContent,
  PopoverHeader,
  PopoverBody,
  PopoverFooter,
  PopoverArrow,
  PopoverCloseButton,
  PopoverAnchor,
  Portal,
  Center,
  useToast, Spinner, Card } from "@chakra-ui/react";
import { useState, useEffect, useRef } from "react";
import { BASE_URL } from "../App";
import { SECRET} from "../App";
import { signPayload } from "../App";
import CountdownTimer from "./CountdownTimer";

const ReserveSeatsButton = ({ seats, setSeats, price, reserved, setReserved, reservedServer, setReservedServer, setRefreshTrigger}) => {
    const { isOpen, onOpen, onClose } = useDisclosure();
	const {
		isOpen: isModalOpen,
		onOpen : onModalOpen,
		onClose : onModalClose, 
	} = useDisclosure();

	const {
		isOpen: isCancellingOpen,
		onOpen : onCancellingOpen,
		onClose : onCancellingClose, 
	} = useDisclosure();


	const [isLoading, setIsLoading] = useState(false);
	const [isWatingForWebSocket, setIsWaitingForWebSocket] = useState(false);
	const [isCancellingTickets, setIsCancellingTickets] = useState(false);
	const [timeToExpire, setTimeToExpire] = useState("");
	const [inputs, setInputs] = useState({
		seatId: "",
		buyerName: "",
		buyerEmail: "",
		buyerPhone: "",
	});
	const toast = useToast();
	const reservedServerRef = useRef(reservedServer);

	const [refreshTriggerEuPago, setRefreshTriggerEuPago] = useState(0);

    const handleReserveSeats = async () => {
        setIsLoading(true);
		//console.log(reserved);
       	
		if (reserved.length == 0){
			toast({
                status: "error",
                title: "Ocorreu um erro.",
                description: "Por favor selecione 1 ou mais lugares.",
                duration: 4000,
            });
			setInputs({
				seatId: "",
				buyerName: "",
				buyerEmail: "",
				buyerPhone: "",
			}); // clear inputs
			return;
		}
        
        try {
			const payload = JSON.stringify({seatIds: reserved});
			const signature = signPayload(payload, SECRET);
            const seatResponse = await fetch(BASE_URL + "/seats/reserves", {
                method: "POST",
                headers: {
                "Content-Type": "application/json",
				"X-Signature": signature,
            },
            body: JSON.stringify({seatIds: reserved}),
            });
            const seatInfo = await seatResponse.json();
            if (!seatResponse.ok) {
                throw new Error(seatInfo.error);
            }

            //Storing the token
            sessionStorage.setItem("token", seatInfo["token"]);
            toast({
                status: "success",
                title: "Yayy! 🎉",
                description: "Bilhetes escolhidos!",
                duration: 1000,
                position: "top-center",
            });

			const seatIds = seatInfo["seats"].map(seat => seat.id);
			setReservedServer(seatIds);
            onOpen();

        } catch (error) {
            toast({
                status: "error",
                title: "Ocorreu um erro.",
                description: error.message,
                duration: 4000,
            });
			handleCloseModal();
			setReserved([]);
        } finally {
            setIsLoading(false);
        }

    };

	const validate = () => {
		const newErrors = [];
		if (!inputs["buyerName"].trim()) {
		newErrors.push("Name is required.");
		}

		if (!inputs["buyerEmail"].trim()) {
		newErrors.push("Email is required.");
		} else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(inputs["buyerEmail"])) {
		newErrors.push("Email is not valid.");
		}

		if (!inputs["buyerPhone"].trim()) {
		newErrors.push("Phone number is required.");
		} else if (!/^\d{9}$/.test(inputs["buyerPhone"])) {
		newErrors.push("Phone number must be exactly 9 digits.");
		}

		return newErrors;
  	};



    const handleBookSeat = async (e) => {
		const validationErrors = validate();
		const numErrors = validationErrors.length; 
		e.preventDefault();
		if (numErrors > 0) {
			for (let i=0; i< numErrors ; i++){
				toast({
					status: "error",
					title: "An error occurred.",
					description: "Please fill the fields correctly: " + validationErrors[i],
					duration: 4000,
				});
			}
		
			setInputs({
				seatId: "",
				buyerName: "",
				buyerEmail: "",
				buyerPhone: "",
			}); // clear inputs
		
		} else {
			setIsLoading(true);
			inputs["seatId"] = reserved;
			inputs["buyerPhone"] = "+351 " + inputs["buyerPhone"];
		
			const token = sessionStorage.getItem("token");

			var ws = new WebSocket("wss://"+ BASE_URL.split("//")[1] +"/notifications?token=" + token);

			ws.onopen = function(e) {
				ws.send("Hello from client!");
			};

			ws.onmessage = function(event) {
				console.log(event.data);

				if(event.data == "Payment complete!"){
					toast({
						status: "success",
						title: "Yayy!",
						description: "Bilhetes comprados e enviados para o seu email!",
						duration: 1000,
						position: "top-center",
						});

					ws.close();

					setReserved([]);
					setReservedServer([]);

					setIsWaitingForWebSocket(false);
					onModalClose();

					setRefreshTrigger((prev) => prev +1);

				}
			};

			try {
				const payload = JSON.stringify(inputs);
				const signature = signPayload(payload, SECRET);
				const res = await fetch(BASE_URL + "/seats/initPaymentSeats", {
					method: "POST",
					headers: {
						"Content-Type": "application/json",
						"Authorization": "Bearer " + token,
						"X-Signature": signature,  
					},
					body: JSON.stringify(inputs),
				});
				const data = await res.json();
				if (!res.ok) {
					throw new Error(data.error);
				}

				const expireTime = data["seats"][0]["reservationExpiresAt"];
				//console.log(data["seats"][0]["reservationExpiresAt"]);
				setTimeToExpire(expireTime);

				toast({
					status: "success",
					title: "Yayy!",
					description: "Bilhetes reservados!",
					duration: 1000,
					position: "top-center",
				});

				setIsWaitingForWebSocket(true);
				onModalOpen();

				setInputs({
					seatId: "",
					buyerName: "",
					buyerEmail: "",
					buyerPhone: "",
				}); // clear inputs
				
				//setIsWaitingForWebSocket(true);
			

				onClose();

			} catch (error) {
				toast({
					status: "error",
					title: "Occoreu um erro.",
					description: error.message,
					duration: 4000,
					position: "top-center",
				});
				handleCloseModal();
			} finally {
				setIsLoading(false);
				//await waitForWebSocketMessage(ws);
			}
		}
		
		
	};

	const calculatePrice = () => {
		return [reservedServer.length, reservedServer.length * price];
	};

    const handleCloseModal = async () => {
		const latestReservedSeats = reservedServerRef.current;
		if (latestReservedSeats.length == 0) {
			setReserved([]);
			if(isOpen){
				onClose();
			}
			if(isModalOpen){
				onModalClose();
			}
			setRefreshTrigger((prev) => prev +1);
		} else {
			var token = sessionStorage.getItem("token");
			try {
				const payload = JSON.stringify({seatIds: latestReservedSeats});
				const signature = signPayload(payload, SECRET);
				const res = await fetch(BASE_URL + "/seats/cancelSeats", {
					method: "POST",
					headers: {
						"Content-Type": "application/json",
						"Authorization": "Bearer " + token,
						"X-Signature": signature,  
					},
					body: JSON.stringify({seatIds: latestReservedSeats}),
				});
				const data = await res.json();
				if (!res.ok) {
					throw new Error(data.error);
				}
				
				setReserved([]);
				setReservedServer([]);
				setInputs({
					seatId: "",
					buyerName: "",
					buyerEmail: "",
					buyerPhone: "",
				}); // clear inputs

				if(isOpen){
					onClose();
				}
				if(isModalOpen){
					onModalClose();
				}
				setRefreshTrigger((prev) => prev +1);
			} catch (error) {
				toast({
					status: "error",
					title: "An error occurred.",
					description: error.message,
					duration: 4000,
					position: "top-center",
				});
				setReserved([]);
				setReservedServer([]);
			} finally {
				setIsLoading(false);
				setIsCancellingTickets(false);
			}
		}

		
	};


	useEffect(() => {
		reservedServerRef.current = reservedServer;
	}, [reservedServer]);

	useEffect(() => {
		handleCloseModal();
	}, [refreshTriggerEuPago]);



	useEffect(() => {
		const handleBeforeUnload = (e) => {
			e.preventDefault();
			//handleCloseModal();

			// Some browsers require this line to show a confirmation dialog
			e.returnValue = '';
		};

		window.addEventListener("beforeunload", handleBeforeUnload);

		// Cleanup function
		return () => {
			window.removeEventListener("beforeunload", handleBeforeUnload);
		};
	}, []);

	const openCancelling = () =>{
		setIsCancellingTickets(true);
		onCancellingOpen();
	}

	const closeCancelling = () =>{
		setIsCancellingTickets(false);
		onCancellingClose();
	}



    return (
		<>
        <Flex my={4} justifyContent={'flex-end'}>
            <Popover>
				<PopoverTrigger>
                <Button size={'lg'} my={4} backgroundColor='#FF00FF'>
                    <Text
                            fontSize={{ base: "xl", md: "3xl" }}
                            letterSpacing={"1px"}
                            color={'white'}
                            textAlign={"center"}
                        >
                        Comprar
                    </Text>
                </Button>
				</PopoverTrigger>
				<Portal>
					<PopoverContent>
						<PopoverArrow />
						<PopoverHeader>Pretende reservar os lugares selecionados?</PopoverHeader>
						<PopoverCloseButton />
					<PopoverBody>
						<Center>
							<Button colorScheme='blue' onClick={handleReserveSeats}>Reservar</Button>
						</Center>
					</PopoverBody>
					</PopoverContent>
				</Portal>
			</Popover>

			<Modal isOpen={isOpen} onClose={handleCloseModal}>
				<ModalOverlay />
				<form onSubmit={handleBookSeat}>
					<ModalContent>
						<ModalHeader>Reserva de bilhetes</ModalHeader>
						<ModalCloseButton />
						<ModalBody>
							<Flex alignItems={"center"} gap={4}>
								<FormControl>
									<FormLabel>Nome completo</FormLabel>
									<Input
										placeholder='Nome Completo'
										value={inputs.buyerName}
										onChange={(e) => setInputs((prev) => ({ ...prev, buyerName: e.target.value }))}
									/>
								</FormControl>
					
								<FormControl>
									<FormLabel>Número de Telemóvel</FormLabel>
									<Input
										placeholder='Número de telemóvel'
										value={inputs.buyerPhone}
										onChange={(e) => setInputs((prev) => ({ ...prev, buyerPhone: e.target.value }))}
									/>
								</FormControl>
							</Flex>
							<FormControl mt={4}>
								<FormLabel>Email</FormLabel>
								<Input
									placeholder="Email"
									value={inputs.buyerEmail}
									onChange={(e) => setInputs((prev) => ({ ...prev, buyerEmail: e.target.value }))}
								/>
							</FormControl>
							<FormControl mt={4}>
								<FormLabel>Preço total</FormLabel>
								<Text>{price} € x {calculatePrice()[0]} bilhetes = {calculatePrice()[1]} €</Text>
							</FormControl>
							
						</ModalBody>

						<ModalFooter>
							<Button colorScheme='blue' mr={3} type='submit' isLoading={isLoading}>
							Reservar Bilhete
							</Button>
							<Button colorScheme='blue' mr={3} onClick={handleCloseModal}>
							Fechar
							</Button>
						</ModalFooter>
					</ModalContent>
				</form>
			</Modal>
            
        </Flex>

		{isWatingForWebSocket && (<Modal closeOnOverlayClick={false} closeOnEsc={false} isOpen={isModalOpen} onClose={onModalClose}>
					<ModalOverlay />
						<ModalContent>
						<ModalHeader>Termine a sua reserva</ModalHeader>
						<ModalBody>
							Conclua o pagamento no MBWay, ou cancele o seu pedido
						</ModalBody>
						<CountdownTimer finishTimeString={timeToExpire} setRefreshTriggerEuPago={setRefreshTriggerEuPago} />

						<ModalFooter>
							<Button onClick={openCancelling} variant='ghost'>Cancelar Reserva</Button>
						</ModalFooter>
						</ModalContent>
				</Modal>)}

		{isCancellingTickets && (<Modal closeOnOverlayClick={false} closeOnEsc={false} isOpen={isCancellingOpen} onClose={onCancellingClose}>
					<ModalOverlay />
						<ModalContent>
						<ModalHeader>Tem a certeza que pretende cancelar a sua reserva?</ModalHeader>
						<ModalFooter>
							<Button colorScheme='blue' mr={3} onClick={handleCloseModal}>Sim</Button>
							<Button onClick={closeCancelling} variant='ghost'>Não</Button>
						</ModalFooter>
						</ModalContent>
				</Modal>)}

		</>
		
		
	);

};

export default ReserveSeatsButton;
