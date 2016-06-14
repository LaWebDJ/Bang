package com.qallta.bang.RecyclerView;

public class Transferencia {
	
	private String monto;
	private String telefono;
	private String fechaRegistro;

	public Transferencia() {
	}

	public Transferencia(String monto, String fechaRegistro, String telefono) {

		this.monto = monto;
		this.fechaRegistro = fechaRegistro;
		this.telefono = telefono;
	}


	public String getMonto() {
		return monto;
	}
	public void setMonto(String monto) {
		this.monto = monto;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(String fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

}
