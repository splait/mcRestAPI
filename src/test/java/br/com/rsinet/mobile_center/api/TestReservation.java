package br.com.rsinet.mobile_center.api;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.Test;

import br.com.rsinet.mobile_center.api.model.DeviceContent;
import br.com.rsinet.mobile_center.api.model.Reservation;
import br.com.rsinet.mobile_center.api.rest.APIClient;

public class TestReservation {

	private static final String USERNAME = "guilherme.sousa@rsinet.com.br";
	private static final String PASSWORD = "Senha1234";

	@Test
	public void reservarDispositivo() throws IOException {
		APIClient api = new APIClient("10.1.15.10", "8080", USERNAME, PASSWORD);
		for (DeviceContent device : api.getAllDevicesInformation()) {
			if (device.isFree() && device.getConnected()) {
				api.createReservation(device.getUdid());
				System.out.println(
						String.format("Celular '%s' reservado para '%s'", device.getUdid(), api.getUserName()));
				break;
			}
		}
	}

	@Test
	public void obterTodosOsDispositivosReservados() throws IOException {
		APIClient api = new APIClient("10.1.15.10", "8080", USERNAME, PASSWORD);
		List<Reservation> allReservationInformation = api.getAllReservationInformation();
		if (allReservationInformation.isEmpty())
			System.out.println("No reservations from user " + api.getUserName());
		for (Reservation reservation : allReservationInformation) {
			System.out.println("User: " + reservation.getReservedForUser().getName() + "\nReservation ID: "
					+ reservation.getReservationUid() + "\nDevice: "
					+ reservation.getDeviceCapabilities().getDeviceName());
		}
	}

	@Test
	public void obterTodosOsDispositivosReservadorPorUsuario() throws IOException {
		APIClient api = new APIClient("10.1.15.10", "8080", USERNAME, PASSWORD);
		List<Reservation> allReservationInformation = api.getAllReservationInformation(USERNAME);
		if (allReservationInformation.isEmpty())
			System.out.println("No reservations from user " + api.getUserName());
		System.out.println("All reservations from user " + api.getUserName());

		for (Reservation reservation : allReservationInformation) {
			System.out.println("Reservation ID: " + reservation.getReservationUid() + "\tDevice: "
					+ reservation.getDeviceCapabilities().getDeviceName());
		}
	}

	@Test
	public void removerTodosOsDispositivosReservadosPorUsuario() throws IOException {
		APIClient api = new APIClient("10.1.15.10", "8080", USERNAME, PASSWORD);
		System.out.println("Removing all reservations by user " + api.getUserName());
		List<Reservation> allReservationInformation = api.getAllReservationInformation(USERNAME);
		if (allReservationInformation.isEmpty())
			System.out.println("No reservations from user " + api.getUserName());
		for (Reservation reservation : allReservationInformation) {
			System.out.println("Reservation ID: " + reservation.getReservationUid() + "\nDevice: "
					+ reservation.getDeviceCapabilities().getDeviceName());
			api.removeReservation(reservation.getReservationUid());
		}
	}
}
